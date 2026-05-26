package com.thrivio.data.local.dao

import androidx.room.*
import com.thrivio.data.local.entity.MealEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Query("SELECT * FROM local_meals WHERE date = :date ORDER BY createdAt DESC")
    fun getMealsForDate(date: String): Flow<List<MealEntity>>

    @Query("SELECT * FROM local_meals WHERE isSynced = 0")
    suspend fun getAllUnsyncedMeals(): List<MealEntity>

    @Query("UPDATE local_meals SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: Long)

    @Upsert
    suspend fun upsertMeal(meal: MealEntity)

    @Query("DELETE FROM local_meals WHERE isSynced = 0 AND createdAt < :cutoffTime")
    suspend fun deleteUnsyncedOlderThan(cutoffTime: Long)
}
