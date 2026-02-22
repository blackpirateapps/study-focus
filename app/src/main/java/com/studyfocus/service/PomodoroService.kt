package com.studyfocus.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class PomodoroService : Service() {

    companion object {
        const val CHANNEL_ID = "pomodoro_timer"
        const val NOTIFICATION_ID = 1001
        const val ACTION_START = "com.studyfocus.action.START_TIMER"
        const val ACTION_STOP = "com.studyfocus.action.STOP_TIMER"
        const val EXTRA_TASK_NAME = "task_name"
        const val EXTRA_DURATION = "duration"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val taskName = intent.getStringExtra(EXTRA_TASK_NAME) ?: "Focus"
                startForeground(NOTIFICATION_ID, createNotification(taskName, "Timer running"))
            }
            ACTION_STOP -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(com.studyfocus.R.string.notification_channel_pomodoro),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = getString(com.studyfocus.R.string.notification_channel_description)
            setShowBadge(false)
        }
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(title: String, content: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }
}
