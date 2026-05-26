package com.thrivio

import android.app.Application
import com.thrivio.data.local.AppDatabase

class ThrivioApp : Application() {

    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getInstance(this)
    }
}
