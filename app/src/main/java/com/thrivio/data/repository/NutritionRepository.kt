package com.thrivio.data.repository

import com.thrivio.data.local.AppDatabase
import com.thrivio.data.local.entity.MealEntity
import com.thrivio.network.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate

class NutritionRepository(private val db: AppDatabase) {

    fun getMealsForDate(date: String): Flow<List<MealEntity>> {
        return db.mealDao().getMealsForDate(date)
    }

    suspend fun logMeal(
        name: String,
        mealType: String,
        calories: Int,
        protein: Float,
        carbs: Float,
        fat: Float
    ) = withContext(Dispatchers.IO) {
        val today = LocalDate.now().toString()
        val meal = MealEntity(
            name = name,
            mealType = mealType,
            date = today,
            totalCalories = calories,
            totalProtein = protein,
            totalCarbs = carbs,
            totalFat = fat
        )
        db.mealDao().upsertMeal(meal)

        try {
            SupabaseClient.client.postgrest.from("nutrition").upsert(
                mapOf(
                    "user_id" to null,
                    "date" to today,
                    "total_calories" to calories,
                    "total_protein" to protein,
                    "total_carbs" to carbs,
                    "total_fat" to fat,
                    "updated_at" to Instant.now().toString()
                )
            )
        } catch (_: Exception) { }
    }
}
