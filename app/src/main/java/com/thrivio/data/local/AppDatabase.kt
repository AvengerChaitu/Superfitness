package com.thrivio.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.thrivio.data.local.dao.MealDao
import com.thrivio.data.local.dao.MoodDao
import com.thrivio.data.local.dao.StepDao
import com.thrivio.data.local.dao.WorkoutDao
import com.thrivio.data.local.entity.MealEntity
import com.thrivio.data.local.entity.MoodEntity
import com.thrivio.data.local.entity.StepEntity
import com.thrivio.data.local.entity.WorkoutEntity

@Database(
    entities = [
        StepEntity::class,
        MealEntity::class,
        WorkoutEntity::class,
        MoodEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stepDao(): StepDao
    abstract fun mealDao(): MealDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun moodDao(): MoodDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "thrivio_local_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
