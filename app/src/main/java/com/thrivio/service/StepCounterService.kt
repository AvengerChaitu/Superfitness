package com.thrivio.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.thrivio.data.local.AppDatabase
import com.thrivio.data.local.entity.StepEntity
import com.thrivio.network.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.*
import java.time.Instant
import java.time.LocalDate

class StepCounterService : Service(), SensorEventListener {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    private val db by lazy { AppDatabase.getInstance(this) }

    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null

    private var initialStepsBootOffset = -1L
    private var totalStepsToday = 0
    private var lastSavedSteps = 0

    companion object {
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "thrivio_notifications"
        const val CHANNEL_NAME = "Thrivio Activity Tracker"
    }

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildActivityNotification("Tracking your steps..."))

        stepCounterSensor?.let { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || event.sensor.type != Sensor.TYPE_STEP_COUNTER) return

        val totalStepsSinceBoot = event.values[0].toLong()

        if (initialStepsBootOffset == -1L) {
            initialStepsBootOffset = totalStepsSinceBoot
        }

        totalStepsToday = (totalStepsSinceBoot - initialStepsBootOffset).toInt()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(
            NOTIFICATION_ID,
            buildActivityNotification("$totalStepsToday steps today!")
        )

        if (totalStepsToday - lastSavedSteps >= 50) {
            lastSavedSteps = totalStepsToday
            persistSteps(totalStepsToday)
        }
    }

    private fun persistSteps(steps: Int) {
        serviceScope.launch {
            try {
                val today = LocalDate.now().toString()

                db.stepDao().upsertSteps(
                    StepEntity(date = today, steps = steps)
                )

                SupabaseClient.client.postgrest.from("step_counts").upsert(
                    mapOf(
                        "date" to today,
                        "steps" to steps,
                        "calories" to 0,
                        "distance_meters" to 0.0,
                        "updated_at" to Instant.now().toString()
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun buildActivityNotification(text: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Thrivio Tracker")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_menu_myplaces)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Step tracking in background"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        serviceJob.cancel()
    }
}
