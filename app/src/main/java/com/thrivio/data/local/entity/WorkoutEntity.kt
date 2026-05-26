package com.thrivio.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val remoteId: String = "",
    val name: String,
    val durationSeconds: Int = 0,
    val caloriesBurned: Int = 0,
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
