package com.studyfocus.ui.screens.pomodoro

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studyfocus.ui.components.PomodoroRing
import com.studyfocus.ui.theme.Dimens

@Composable
fun PomodoroScreen(
    taskId: Long = 0,
    viewModel: PomodoroViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(taskId) {
        if (taskId > 0) viewModel.loadTask(taskId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.SpacingXxl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(Dimens.SpacingHuge))

        // Header
        Text(
            text = if (uiState.isBreak) "Break Time" else "Focus",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.weight(1f))

        // Timer ring
        val progress = if (uiState.totalSeconds > 0) {
            uiState.remainingSeconds.toFloat() / uiState.totalSeconds.toFloat()
        } else 0f

        PomodoroRing(
            progress = progress,
            timeText = viewModel.formatTime(uiState.remainingSeconds),
            sessionText = if (uiState.isBreak) "Break"
            else "Session ${uiState.currentSession} of ${uiState.totalSessions}",
            taskName = uiState.task?.title ?: "",
            progressColor = if (uiState.isBreak) MaterialTheme.colorScheme.tertiary
            else MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.weight(1f))

        // Controls
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingXxl),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reset button
            FilledTonalIconButton(
                onClick = { viewModel.resetTimer() },
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Reset",
                    modifier = Modifier.size(Dimens.IconMd)
                )
            }

            // Play/Pause button
            FloatingActionButton(
                onClick = {
                    when (uiState.timerState) {
                        TimerState.IDLE, TimerState.PAUSED -> viewModel.startTimer()
                        TimerState.RUNNING -> viewModel.pauseTimer()
                        TimerState.COMPLETED -> viewModel.resetTimer()
                        TimerState.BREAK -> viewModel.startTimer()
                    }
                },
                modifier = Modifier.size(72.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = if (uiState.timerState == TimerState.RUNNING)
                        Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (uiState.timerState == TimerState.RUNNING)
                        "Pause" else "Start",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            // Spacer to balance layout
            Spacer(modifier = Modifier.size(56.dp))
        }

        Spacer(modifier = Modifier.height(Dimens.SpacingHuge))

        // Today's stats
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Text(
                text = "🍅 ${uiState.completedTodayCount} completed today",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(
                    horizontal = Dimens.SpacingLg,
                    vertical = Dimens.SpacingSm
                )
            )
        }

        Spacer(modifier = Modifier.height(Dimens.SpacingHuge))
    }
}
