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
import com.thrivio.network.SupabaseClient
import io.github.jan.tennert.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StepCounterService : Service(), SensorEventListener {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null

    private var initialStepsBootOffset = -1
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
        startForeground(NOTIFICATION_ID, buildActivityNotification("Initializing Aero Step Tracker..."))

        // Register steps sensor listener
        stepCounterSensor?.let { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || event.sensor.type != Sensor.TYPE_STEP_COUNTER) return

        val totalStepsSinceBoot = event.values[0].toInt()

        if (initialStepsBootOffset == -1) {
            initialStepsBootOffset = totalStepsSinceBoot
            // Pull any existing saved steps from local database or shared prefs if needed
        }

        totalStepsToday = totalStepsSinceBoot - initialStepsBootOffset
        
        // Update notification in real-time
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, buildActivityNotification("Aero tracked $totalStepsToday steps today!"))

        // Debounce database sync (save if steps grew by more than 50 steps)
        if (totalStepsToday - lastSavedSteps >= 50) {
            lastSavedSteps = totalStepsToday
            syncStepsToSupabase(totalStepsToday)
        }
    }

    private fun syncStepsToSupabase(steps: Int) {
        serviceScope.launch {
            try {
                val userId = SupabaseClient.client.postgrest.postgrest.currentSerializer // Handled via session
                val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                
                // Construct basic payload (upsert steps history)
                // In production, GoTrue active user ID is used automatically
                // Here we upsert steps for the current date
                // SupabaseClient.client.postgrest["step_counts"].upsert(...)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun buildActivityNotification(text: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Thrivio Active Tracker")
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
                description = "Keeps Aero step tracking running in the background"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        serviceJob.cancel()
    }
}
