package com.studyfocus.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.studyfocus.ui.screens.pomodoro.TimerState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class PomodoroService : Service() {

    @Inject
    lateinit var timerManager: PomodoroTimerManager

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    companion object {
        const val CHANNEL_ID = "pomodoro_timer"
        const val NOTIFICATION_ID = 1001
        const val ACTION_START = "com.studyfocus.action.START_TIMER"
        const val ACTION_STOP = "com.studyfocus.action.STOP_TIMER"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        serviceScope.launch {
            timerManager.uiState.collect { state ->
                if (state.timerState == TimerState.RUNNING || state.timerState == TimerState.PAUSED) {
                    val title = state.task?.title ?: "Focus"
                    val timeStr = formatTime(state.remainingSeconds)
                    val status = if (state.timerState == TimerState.PAUSED) "Paused" else "Running"
                    updateNotification(title, "$timeStr - $status")
                } else if (state.timerState == TimerState.IDLE || state.timerState == TimerState.COMPLETED) {
                    if (state.isBreak) {
                        val timeStr = formatTime(state.remainingSeconds)
                        updateNotification("Break Time", "$timeStr - Take a break!")
                    } else {
                        stopForeground(STOP_FOREGROUND_REMOVE)
                        stopSelf()
                    }
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val state = timerManager.uiState.value
                val title = state.task?.title ?: "Focus"
                startForeground(NOTIFICATION_ID, createNotification(title, "Starting..."))
            }
            ACTION_STOP -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
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

    private fun updateNotification(title: String, content: String) {
        val notification = createNotification(title, content)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun formatTime(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return "%02d:%02d".format(mins, secs)
    }
}
