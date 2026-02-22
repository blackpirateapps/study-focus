package com.studyfocus.ui.screens.pomodoro

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyfocus.data.model.PomodoroSession
import com.studyfocus.data.model.Task
import com.studyfocus.data.repository.PomodoroRepository
import com.studyfocus.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import com.studyfocus.service.PomodoroTimerManager

enum class TimerState {
    IDLE, RUNNING, PAUSED, BREAK, COMPLETED
}

data class PomodoroUiState(
    val task: Task? = null,
    val timerState: TimerState = TimerState.IDLE,
    val remainingSeconds: Int = 25 * 60,
    val totalSeconds: Int = 25 * 60,
    val currentSession: Int = 1,
    val totalSessions: Int = 2,
    val isBreak: Boolean = false,
    val completedTodayCount: Int = 0
)

@HiltViewModel
class PomodoroViewModel @Inject constructor(
    private val timerManager: PomodoroTimerManager,
    private val pomodoroRepository: PomodoroRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val uiState: StateFlow<PomodoroUiState> = timerManager.uiState

    init {
        val taskId = savedStateHandle.get<Long>("taskId")
        if (taskId != null && taskId > 0) {
            timerManager.loadTask(taskId)
        }

        // We can just rely on timerManager's state for completedTodayCount
        // which it initializes on its own
    }

    fun loadTask(taskId: Long) = timerManager.loadTask(taskId)
    fun startTimer() = timerManager.startTimer()
    fun pauseTimer() = timerManager.pauseTimer()
    fun resetTimer() = timerManager.resetTimer()

    fun formatTime(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return "%02d:%02d".format(mins, secs)
    }

    override fun onCleared() {
        super.onCleared()
    }
}
