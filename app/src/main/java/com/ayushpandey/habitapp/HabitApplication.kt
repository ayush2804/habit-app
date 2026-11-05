package com.ayushpandey.habitapp

import android.app.Application
import com.ayushpandey.habitapp.data.HabitDatabase
import com.ayushpandey.habitapp.data.HabitRepository

class HabitApplication : Application() {
    lateinit var repository: HabitRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val db = HabitDatabase.get(this)
        repository = HabitRepository(db.habitDao())
    }
}
