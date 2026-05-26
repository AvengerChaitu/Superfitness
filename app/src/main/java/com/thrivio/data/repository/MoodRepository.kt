package com.thrivio.data.repository

import com.thrivio.data.local.AppDatabase
import com.thrivio.data.local.entity.MoodEntity

class MoodRepository(private val db: AppDatabase) {

    suspend fun logMood(date: String, mood: Int, note: String = "") {
        db.moodDao().upsertMood(MoodEntity(date = date, mood = mood, note = note))
    }
}
