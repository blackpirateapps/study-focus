package com.studyfocus.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studyfocus.ui.components.ProjectCard
import com.studyfocus.ui.components.TaskListItem
import com.studyfocus.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToProject: (Long) -> Unit,
    onNavigateToPomodoro: (Long) -> Unit,
    onNavigateToCreateTask: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateTask,
                containerColor = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Task")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(vertical = Dimens.SpacingLg)
        ) {
            // Header
            item {
                Text(
                    text = "Home",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(
                        horizontal = Dimens.SpacingXxl,
                        vertical = Dimens.SpacingLg
                    )
                )
            }

            // Projects section
            if (uiState.projects.isNotEmpty()) {
                item {
                    Text(
                        text = "Projects",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(
                            horizontal = Dimens.SpacingXxl,
                            vertical = Dimens.SpacingSm
                        )
                    )
                }

                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = Dimens.SpacingXxl),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)
                    ) {
                        items(uiState.projects) { projectWithTasks ->
                            ProjectCard(
                                name = projectWithTasks.project.name,
                                colorHex = projectWithTasks.project.colorHex,
                                taskCount = projectWithTasks.tasks.count { !it.isCompleted },
                                onClick = { onNavigateToProject(projectWithTasks.project.id) }
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(Dimens.SpacingXl)) }
            }

            // Recent tasks section
            if (uiState.recentTasks.isNotEmpty()) {
                item {
                    Text(
                        text = "Recent Tasks",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(
                            horizontal = Dimens.SpacingXxl,
                            vertical = Dimens.SpacingSm
                        )
                    )
                }

                items(
                    items = uiState.recentTasks,
                    key = { it.task.id }
                ) { taskWithDetails ->
                    val project = uiState.projects.find {
                        it.project.id == taskWithDetails.task.projectId
                    }
                    val projectColor = project?.let {
                        try {
                            Color(android.graphics.Color.parseColor(it.project.colorHex))
                        } catch (e: Exception) { null }
                    }

                    TaskListItem(
                        taskWithDetails = taskWithDetails,
                        projectColor = projectColor,
                        onTaskClick = { },
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
            if (!uiState.isLoading && uiState.recentTasks.isEmpty() && uiState.projects.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimens.SpacingMassive)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No tasks yet",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(Dimens.SpacingSm))
                            Text(
                                text = "Tap + to create your first task",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
