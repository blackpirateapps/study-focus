package com.studyfocus.ui.screens.today

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studyfocus.ui.components.TaskListItem
import com.studyfocus.ui.theme.Dimens
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TodayScreen(
    onNavigateToPomodoro: (Long) -> Unit,
    onNavigateToEditTask: (Long) -> Unit,
    viewModel: TodayViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val today = LocalDate.now()
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = Dimens.SpacingLg)
    ) {
        // Header
        item {
            Column(
                modifier = Modifier.padding(
                    horizontal = Dimens.SpacingXxl,
                    vertical = Dimens.SpacingLg
                )
            ) {
                Text(
                    text = "Today",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(modifier = Modifier.height(Dimens.SpacingXs))
                Text(
                    text = today.format(dateFormatter),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // Task list
        if (uiState.todayTasks.isNotEmpty()) {
            items(
                items = uiState.todayTasks,
                key = { it.task.id }
            ) { taskWithDetails ->
                TaskListItem(
                    taskWithDetails = taskWithDetails,
                    onTaskClick = { onNavigateToEditTask(taskWithDetails.task.id) },
                    onCheckClick = {
                        viewModel.toggleTaskCompletion(
                            taskWithDetails.task.id,
                            taskWithDetails.task.isCompleted
                        )
                    },
                    onPlayClick = { onNavigateToPomodoro(taskWithDetails.task.id) },
                    modifier = Modifier.padding(
                        horizontal = Dimens.SpacingLg,
                        vertical = Dimens.SpacingXxs
                    )
                )
            }
        }

        // Empty state
        if (!uiState.isLoading && uiState.todayTasks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.SpacingMassive),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "All clear!",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(Dimens.SpacingSm))
                        Text(
                            text = "No tasks due today",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
