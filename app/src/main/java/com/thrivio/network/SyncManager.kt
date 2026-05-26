package com.thrivio.network

import android.content.Context
import com.thrivio.data.local.AppDatabase
import com.thrivio.data.local.entity.StepEntity
import com.thrivio.data.local.entity.MealEntity
import com.thrivio.data.local.entity.WorkoutEntity
import com.thrivio.data.local.entity.MoodEntity
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.time.Instant

class SyncManager(private val context: Context) {

    private val db = AppDatabase.getInstance(context)

    suspend fun syncAll() = withContext(Dispatchers.IO) {
        try {
            val session = SupabaseClient.client.auth.currentSessionOrNull()
            if (session == null) return@withContext
            val userId = session.user?.id ?: return@withContext

            coroutineScope {
                listOf(
                    async { syncSteps(userId) },
                    async { syncMeals(userId) },
                    async { syncWorkouts(userId) }
                ).awaitAll()
            }
        } catch (_: Exception) { }
    }

    private suspend fun syncSteps(userId: String) {
        val steps = db.stepDao().getAllUnsyncedSteps()
        for (step in steps) {
            try {
                SupabaseClient.client.postgrest.from("step_counts").upsert(
                    mapOf(
                        "user_id" to userId,
                        "date" to step.date,
                        "steps" to step.steps,
                        "updated_at" to Instant.now().toString()
                    )
                )
                db.stepDao().markSynced(step.id)
            } catch (_: Exception) { }
        }
    }

    private suspend fun syncMeals(userId: String) {
        val meals = db.mealDao().getAllUnsyncedMeals()
        for (meal in meals) {
            try {
                SupabaseClient.client.postgrest.from("nutrition").upsert(
                    mapOf(
                        "user_id" to userId,
                        "date" to meal.date,
                        "total_calories" to meal.totalCalories,
                        "total_protein" to meal.totalProtein.toDouble(),
                        "total_carbs" to meal.totalCarbs.toDouble(),
                        "total_fat" to meal.totalFat.toDouble(),
                        "updated_at" to Instant.now().toString()
                    )
                )
                db.mealDao().markSynced(meal.id)
            } catch (_: Exception) { }
        }
    }

    private suspend fun syncWorkouts(userId: String) {
        val workouts = db.workoutDao().getAllUnsyncedWorkouts()
        for (w in workouts) {
            try {
                SupabaseClient.client.postgrest.from("workouts").upsert(
                    mapOf(
                        "user_id" to userId,
                        "name" to w.name,
                        "duration_seconds" to w.durationSeconds,
                        "calories_burned" to w.caloriesBurned,
                        "updated_at" to Instant.now().toString()
                    )
                )
                db.workoutDao().markSynced(w.id)
            } catch (_: Exception) { }
        }
    }
}
