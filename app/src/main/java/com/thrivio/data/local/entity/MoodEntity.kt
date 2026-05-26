package com.thrivio.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_mood_logs")
data class MoodEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val mood: Int,
    val note: String = "",
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
