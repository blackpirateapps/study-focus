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
    private val taskRepository: TaskRepository,
    private val pomodoroRepository: PomodoroRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(PomodoroUiState())
    val uiState: StateFlow<PomodoroUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var sessionStartTime: Long = 0L

    init {
        val taskId = savedStateHandle.get<Long>("taskId")
        if (taskId != null && taskId > 0) {
            loadTask(taskId)
        }

        viewModelScope.launch {
            pomodoroRepository.getCompletedTodayCount().collect { count ->
                _uiState.update { it.copy(completedTodayCount = count) }
            }
        }
    }

    fun loadTask(taskId: Long) {
        viewModelScope.launch {
            val task = taskRepository.getTaskByIdOnce(taskId)
            if (task != null) {
                _uiState.update {
                    it.copy(
                        task = task,
                        totalSeconds = task.pomodoroDurationMinutes * 60,
                        remainingSeconds = task.pomodoroDurationMinutes * 60,
                        totalSessions = task.pomodoroCount,
                        currentSession = task.completedPomodoros + 1,
                        timerState = TimerState.IDLE
                    )
                }
            }
        }
    }

    fun startTimer() {
        if (_uiState.value.timerState == TimerState.RUNNING) return
        sessionStartTime = System.currentTimeMillis()

        _uiState.update { it.copy(timerState = TimerState.RUNNING) }
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.remainingSeconds > 0 && isActive) {
                delay(1000L)
                _uiState.update { it.copy(remainingSeconds = it.remainingSeconds - 1) }
            }

            if (_uiState.value.remainingSeconds <= 0) {
                onTimerComplete()
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(timerState = TimerState.PAUSED) }
    }

    fun resetTimer() {
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                timerState = TimerState.IDLE,
                remainingSeconds = if (it.isBreak) getBreakDuration(it) else it.totalSeconds,
            )
        }
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
            return
        }

        // Work session completed — save it
        viewModelScope.launch {
            val session = PomodoroSession(
                taskId = state.task?.id ?: 0,
                startTime = sessionStartTime,
                endTime = System.currentTimeMillis(),
                durationMinutes = state.totalSeconds / 60,
                isCompleted = true
            )
            pomodoroRepository.insertSession(session)

            // Update task's completed pomodoro count
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
        }
    }

    private fun getBreakDuration(state: PomodoroUiState): Int {
        return if (state.currentSession >= state.totalSessions) 15 * 60 else 5 * 60
    }

    fun formatTime(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return "%02d:%02d".format(mins, secs)
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }
}
