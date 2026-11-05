package com.ayushpandey.habitapp.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.ayushpandey.habitapp.data.HabitEntity
import java.util.Calendar

object ReminderScheduler {
    const val EXTRA_HABIT_ID = "habit_id"
    const val EXTRA_HABIT_TITLE = "habit_title"

    fun schedule(context: Context, habit: HabitEntity) {
        if (!habit.reminderEnabled || habit.archived) {
            cancel(context, habit.id)
            return
        }
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerAt = nextTriggerMillis(habit.reminderHour, habit.reminderMinute)
        val pi = pendingIntent(context, habit)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
            }
        } catch (_: SecurityException) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
        }
    }

    fun cancel(context: Context, habitId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, HabitReminderReceiver::class.java)
        val pi = PendingIntent.getBroadcast(
            context,
            habitId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pi)
    }

    fun rescheduleAll(context: Context, habits: List<HabitEntity>) {
        habits.forEach { schedule(context, it) }
    }

    private fun pendingIntent(context: Context, habit: HabitEntity): PendingIntent {
        val intent = Intent(context, HabitReminderReceiver::class.java).apply {
            putExtra(EXTRA_HABIT_ID, habit.id)
            putExtra(EXTRA_HABIT_TITLE, habit.title)
        }
        return PendingIntent.getBroadcast(
            context,
            habit.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun nextTriggerMillis(hour: Int, minute: Int): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        return cal.timeInMillis
    }
}
