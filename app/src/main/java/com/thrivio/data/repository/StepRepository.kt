package com.thrivio.data.repository

import com.thrivio.data.local.AppDatabase
import com.thrivio.data.local.entity.StepEntity
import com.thrivio.network.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate

class StepRepository(private val db: AppDatabase) {

    fun getTodaySteps(): Flow<StepEntity?> {
        val today = LocalDate.now().toString()
        return db.stepDao().getStepsForDate(today)
    }

    fun getRecentSteps(): Flow<List<StepEntity>> {
        return db.stepDao().getRecentSteps()
    }

    suspend fun syncStepsToSupabase(steps: Int, calories: Int = 0) = withContext(Dispatchers.IO) {
        try {
            SupabaseClient.client.postgrest.from("step_counts").upsert(
                mapOf(
                    "date" to LocalDate.now().toString(),
                    "steps" to steps,
                    "calories" to calories,
                    "updated_at" to Instant.now().toString()
                )
            )
        } catch (_: Exception) { }
    }
}
