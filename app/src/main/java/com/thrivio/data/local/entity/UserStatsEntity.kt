package com.thrivio.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey
    val id: Int = 1,
    val totalXp: Int = 0,
    val currentLevel: Int = 1,
    val streakDays: Int = 0,
    val lastActiveDate: String = "",
    val longestStreak: Int = 0,
    val stepsToday: Int = 0,
    val caloriesToday: Int = 0,
    val mealsLoggedToday: Int = 0,
    val workoutsCompletedToday: Int = 0,
    val meditationMinutesToday: Int = 0
)
