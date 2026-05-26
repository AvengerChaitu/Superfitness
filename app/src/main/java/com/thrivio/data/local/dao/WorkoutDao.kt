package com.thrivio.data.local.dao

import androidx.room.*
import com.thrivio.data.local.entity.WorkoutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM local_workouts ORDER BY createdAt DESC LIMIT 20")
    fun getRecentWorkouts(): Flow<List<WorkoutEntity>>

    @Upsert
    suspend fun upsertWorkout(workout: WorkoutEntity)

    @Query("SELECT COUNT(*) FROM local_workouts WHERE isSynced = 0")
    fun getUnsyncedCount(): Flow<Int>
}
