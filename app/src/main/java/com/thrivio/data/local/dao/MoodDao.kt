package com.thrivio.data.local.dao

import androidx.room.*
import com.thrivio.data.local.entity.MoodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodDao {
    @Query("SELECT * FROM local_mood_logs ORDER BY date DESC LIMIT 30")
    fun getRecentMoods(): Flow<List<MoodEntity>>

    @Upsert
    suspend fun upsertMood(mood: MoodEntity)
}
