package com.ayushpandey.habitapp.reminders

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.ayushpandey.habitapp.HabitApplication
import com.ayushpandey.habitapp.MainActivity
import com.ayushpandey.habitapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HabitReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val habitId = intent.getLongExtra(ReminderScheduler.EXTRA_HABIT_ID, -1L)
        val title = intent.getStringExtra(ReminderScheduler.EXTRA_HABIT_TITLE) ?: "Habit"
        showNotification(context, habitId, title)

        // Reschedule for next day
        val app = context.applicationContext as? HabitApplication ?: return
        if (habitId <= 0) return
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                app.repository.getHabitsWithReminders()
                    .firstOrNull { it.id == habitId }
                    ?.let { ReminderScheduler.schedule(context, it) }
            } finally {
                pending.finish()
            }
        }
    }

    private fun showNotification(context: Context, habitId: Long, title: String) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "habit_reminders"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    "Habit reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
        val open = PendingIntent.getActivity(
            context,
            habitId.toInt(),
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText("Time for your habit — check in when you're ready.")
            .setContentIntent(open)
            .setAutoCancel(true)
            .build()
        manager.notify(habitId.toInt(), notification)
    }
}

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val app = context.applicationContext as? HabitApplication ?: return
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ReminderScheduler.rescheduleAll(context, app.repository.getHabitsWithReminders())
            } finally {
                pending.finish()
            }
        }
    }
}
