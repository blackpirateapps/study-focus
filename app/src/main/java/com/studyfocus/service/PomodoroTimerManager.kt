package com.studyfocus.service

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.studyfocus.data.model.PomodoroSession
import com.studyfocus.data.model.Task
import com.studyfocus.data.repository.PomodoroRepository
import com.studyfocus.data.repository.TaskRepository
import com.studyfocus.ui.screens.pomodoro.PomodoroUiState
import com.studyfocus.ui.screens.pomodoro.TimerState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PomodoroTimerManager @Inject constructor(
    private val taskRepository: TaskRepository,
    private val pomodoroRepository: PomodoroRepository,
    @ApplicationContext private val context: Context
) {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _uiState = MutableStateFlow(PomodoroUiState())
    val uiState: StateFlow<PomodoroUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var sessionStartTime: Long = 0L

    init {
        scope.launch {
            pomodoroRepository.getCompletedTodayCount().collect { count ->
                _uiState.update { it.copy(completedTodayCount = count) }
            }
        }
    }

    fun loadTask(taskId: Long) {
        val currentState = _uiState.value
        // If it's already loaded and playing/paused, don't overwrite
        if (currentState.task?.id == taskId && currentState.timerState != TimerState.IDLE) {
            return
        }
        
        scope.launch {
            val task = taskRepository.getTaskByIdOnce(taskId)
            if (task != null) {
                // If we switch tasks, stop the old timer
                if (currentState.timerState == TimerState.RUNNING) {
                    pauseTimer()
                }
                
                _uiState.update {
                    it.copy(
                        task = task,
                        totalSeconds = task.pomodoroDurationMinutes * 60,
                        remainingSeconds = task.pomodoroDurationMinutes * 60,
                        totalSessions = task.pomodoroCount,
                        currentSession = task.completedPomodoros + 1,
                        timerState = TimerState.IDLE,
                        isBreak = false
                    )
                }
            }
        }
    }

    fun startTimer() {
        if (_uiState.value.timerState == TimerState.RUNNING) return
        sessionStartTime = System.currentTimeMillis()

        _uiState.update { it.copy(timerState = TimerState.RUNNING) }
        startService()

        timerJob?.cancel()
        timerJob = scope.launch(Dispatchers.Default) {
            while (_uiState.value.remainingSeconds > 0 && isActive) {
                delay(1000L)
                _uiState.update { it.copy(remainingSeconds = it.remainingSeconds - 1) }
            }

            if (_uiState.value.remainingSeconds <= 0 && isActive) {
                withContext(Dispatchers.Main) {
                    onTimerComplete()
                }
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(timerState = TimerState.PAUSED) }
        // Keep service running to show paused state, or stop it. We'll update notification.
    }

    fun resetTimer() {
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                timerState = TimerState.IDLE,
                remainingSeconds = if (it.isBreak) getBreakDuration(it) else it.totalSeconds,
            )
        }
        stopService()
    }

    private fun onTimerComplete() {
        val state = _uiState.value

        if (state.isBreak) {
            // Break finished, start next work session
            _uiState.update {
                it.copy(
                    isBreak = false,
                    timerState = TimerState.IDLE,
                    remainingSeconds = it.totalSeconds
                )
            }
            stopService()
            return
        }

        // Work session completed
        scope.launch {
            val session = PomodoroSession(
                taskId = state.task?.id ?: 0,
                startTime = sessionStartTime,
                endTime = System.currentTimeMillis(),
                durationMinutes = state.totalSeconds / 60,
                isCompleted = true
            )
            pomodoroRepository.insertSession(session)

            state.task?.let { task ->
                taskRepository.updateCompletedPomodoros(task.id, state.currentSession)
            }
        }

        if (state.currentSession >= state.totalSessions) {
            // All sessions done
            _uiState.update {
                it.copy(
                    timerState = TimerState.COMPLETED,
                    currentSession = it.totalSessions
                )
            }
            stopService()
        } else {
            // Start break
            val breakDuration = getBreakDuration(state)
            _uiState.update {
                it.copy(
                    isBreak = true,
                    timerState = TimerState.IDLE,
                    currentSession = it.currentSession + 1,
                    remainingSeconds = breakDuration,
                    totalSeconds = breakDuration
                )
            }
            stopService()
        }
    }

    private fun getBreakDuration(state: PomodoroUiState): Int {
        return if (state.currentSession >= state.totalSessions) 15 * 60 else 5 * 60
    }

    private fun startService() {
        val intent = Intent(context, PomodoroService::class.java).apply {
            action = PomodoroService.ACTION_START
        }
        ContextCompat.startForegroundService(context, intent)
    }

    private fun stopService() {
        val intent = Intent(context, PomodoroService::class.java).apply {
            action = PomodoroService.ACTION_STOP
        }
        context.startService(intent) // or stopService(intent)
    }
}
