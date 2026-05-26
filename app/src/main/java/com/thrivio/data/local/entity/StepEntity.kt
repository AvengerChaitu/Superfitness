package com.thrivio.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_step_counts")
data class StepEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val steps: Int = 0,
    val calories: Int = 0,
    val distanceMeters: Float = 0f,
    val isSynced: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
