package com.thrivio.data.local.dao

import androidx.room.*
import com.thrivio.data.local.entity.StepEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StepDao {
    @Query("SELECT * FROM local_step_counts WHERE date = :date LIMIT 1")
    fun getStepsForDate(date: String): Flow<StepEntity?>

    @Query("SELECT * FROM local_step_counts ORDER BY date DESC LIMIT 30")
    fun getRecentSteps(): Flow<List<StepEntity>>

    @Query("SELECT * FROM local_step_counts WHERE isSynced = 0")
    suspend fun getAllUnsyncedSteps(): List<StepEntity>

    @Query("UPDATE local_step_counts SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: Long)

    @Upsert
    suspend fun upsertSteps(step: StepEntity)

    @Query("DELETE FROM local_step_counts WHERE date < :cutoffDate")
    suspend fun deleteOlderThan(cutoffDate: String)
}
