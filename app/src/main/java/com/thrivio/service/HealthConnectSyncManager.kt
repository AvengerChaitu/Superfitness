package com.thrivio.service

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.thrivio.data.local.AppDatabase
import com.thrivio.data.local.entity.StepEntity
import com.thrivio.network.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

class HealthConnectSyncManager(private val context: Context) {

    private val healthClient by lazy { HealthConnectClient.getOrCreate(context) }
    private val db by lazy { AppDatabase.getInstance(context) }

    suspend fun syncSteps(): Int = withContext(Dispatchers.IO) {
        try {
            val now = ZonedDateTime.now()
            val startOfDay = now.toLocalDate().atStartOfDay(ZoneId.systemDefault())

            val response = healthClient.readRecords(
                ReadRecordsRequest(
                    recordType = StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(
                        startOfDay.toInstant(),
                        Instant.now()
                    )
                )
            )
            val totalSteps = response.records.sumOf { it.count }.toInt()

            val dateStr = LocalDate.now().toString()

            db.stepDao().upsertSteps(
                StepEntity(date = dateStr, steps = totalSteps)
            )

            try {
                SupabaseClient.client.postgrest.from("step_counts").upsert(
                    mapOf(
                        "date" to dateStr,
                        "steps" to totalSteps,
                        "calories" to 0,
                        "distance_meters" to 0.0,
                        "updated_at" to Instant.now().toString()
                    )
                )
            } catch (_: Exception) { }

            totalSteps
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    suspend fun syncCalories(): Int = withContext(Dispatchers.IO) {
        try {
            val now = ZonedDateTime.now()
            val startOfDay = now.toLocalDate().atStartOfDay(ZoneId.systemDefault())

            val response = healthClient.readRecords(
                ReadRecordsRequest(
                    recordType = TotalCaloriesBurnedRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(
                        startOfDay.toInstant(),
                        Instant.now()
                    )
                )
            )
            response.records.sumOf { it.energy.inCalories.toInt() }
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    suspend fun syncSleep(): Float = withContext(Dispatchers.IO) {
        try {
            val now = ZonedDateTime.now()
            val startOfLastNight = now.minusHours(16)
                .toLocalDate()
                .atStartOfDay(ZoneId.systemDefault())

            val response = healthClient.readRecords(
                ReadRecordsRequest(
                    recordType = SleepSessionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(
                        startOfLastNight.toInstant(),
                        Instant.now()
                    )
                )
            )
            if (response.records.isEmpty()) return@withContext 0f

            val totalMillis = response.records.sumOf {
                it.endTime.toEpochMilli() - it.startTime.toEpochMilli()
            }
            totalMillis / (1000f * 60f * 60f)
        } catch (e: Exception) {
            e.printStackTrace()
            0f
        }
    }

    suspend fun syncAll() {
        syncSteps()
        syncCalories()
        syncSleep()
    }
}
