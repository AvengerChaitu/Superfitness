package com.thrivio.data.local.dao

import androidx.room.*
import com.thrivio.data.local.entity.WorkoutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM local_workouts ORDER BY createdAt DESC LIMIT 20")
    fun getRecentWorkouts(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM local_workouts WHERE isSynced = 0")
    suspend fun getAllUnsyncedWorkouts(): List<WorkoutEntity>

    @Query("UPDATE local_workouts SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: Long)

    @Upsert
    suspend fun upsertWorkout(workout: WorkoutEntity)

    @Query("SELECT COUNT(*) FROM local_workouts WHERE isSynced = 0")
    fun getUnsyncedCount(): Flow<Int>
}
