package com.thrivio.data.repository

import com.thrivio.data.local.AppDatabase
import com.thrivio.data.local.entity.WorkoutEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class WorkoutRepository(private val db: AppDatabase) {

    fun getRecentWorkouts(): Flow<List<WorkoutEntity>> {
        return db.workoutDao().getRecentWorkouts()
    }

    suspend fun logWorkout(name: String, durationSeconds: Int, caloriesBurned: Int) = withContext(Dispatchers.IO) {
        db.workoutDao().upsertWorkout(
            WorkoutEntity(
                name = name,
                durationSeconds = durationSeconds,
                caloriesBurned = caloriesBurned
            )
        )
    }
}
