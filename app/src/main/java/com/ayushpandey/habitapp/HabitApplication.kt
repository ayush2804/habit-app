package com.ayushpandey.habitapp

import android.app.Application
import com.ayushpandey.habitapp.data.HabitDatabase
import com.ayushpandey.habitapp.data.HabitRepository
import com.ayushpandey.habitapp.reminders.ReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class HabitApplication : Application() {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    lateinit var repository: HabitRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val db = HabitDatabase.get(this)
        repository = HabitRepository(db.habitDao())
        appScope.launch {
            ReminderScheduler.rescheduleAll(this@HabitApplication, repository.getHabitsWithReminders())
        }
    }
}
