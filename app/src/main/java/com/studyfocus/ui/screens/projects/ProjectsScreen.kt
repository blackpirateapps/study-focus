package com.studyfocus.ui.screens.projects

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studyfocus.ui.components.ProjectCard
import com.studyfocus.ui.components.TaskListItem
import com.studyfocus.ui.theme.Dimens
import com.studyfocus.ui.theme.ProjectColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(
    onNavigateToProject: (Long) -> Unit,
    onNavigateToPomodoro: (Long) -> Unit,
    viewModel: ProjectsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Project")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(vertical = Dimens.SpacingLg)
        ) {
            item {
                Text(
                    text = "Projects",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(
                        horizontal = Dimens.SpacingXxl,
                        vertical = Dimens.SpacingLg
                    )
                )
            }

            if (uiState.projects.isNotEmpty()) {
                items(uiState.projects) { projectWithTasks ->
                    ProjectCard(
                        name = projectWithTasks.project.name,
                        colorHex = projectWithTasks.project.colorHex,
                        taskCount = projectWithTasks.tasks.count { !it.isCompleted },
                        onClick = { onNavigateToProject(projectWithTasks.project.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = Dimens.SpacingLg,
                                vertical = Dimens.SpacingXs
                            )
                    )
                }
            }

            if (!uiState.isLoading && uiState.projects.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimens.SpacingMassive),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No projects yet",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(Dimens.SpacingSm))
                            Text(
                                text = "Tap + to create your first project",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateProjectDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, color ->
                viewModel.createProject(name, color)
                showCreateDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    projectId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToPomodoro: (Long) -> Unit,
    onNavigateToCreateTask: () -> Unit,
    onNavigateToEditTask: (Long) -> Unit,
    viewModel: ProjectsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(projectId) {
        viewModel.selectProject(projectId)
    }

    val project = uiState.selectedProject

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = project?.project?.name ?: "",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
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
            contentPadding = PaddingValues(vertical = Dimens.SpacingSm)
        ) {
            items(
                items = uiState.selectedProjectTasks,
                key = { it.task.id }
            ) { taskWithDetails ->
                val projectColor = project?.let {
                    try {
                        Color(android.graphics.Color.parseColor(it.project.colorHex))
                    } catch (e: Exception) { null }
                }

                TaskListItem(
                    taskWithDetails = taskWithDetails,
                    projectColor = projectColor,
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

            if (uiState.selectedProjectTasks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimens.SpacingMassive),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No tasks in this project",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProjectDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(ProjectColors.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("New Project", style = MaterialTheme.typography.headlineSmall)
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Project name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(Dimens.SpacingLg))

                Text(
                    "Color",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(Dimens.SpacingSm))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    modifier = Modifier.height(Dimens.SpacingMassive * 2),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSm),
                    verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)
                ) {
                    items(ProjectColors) { colorHex ->
                        val color = try {
                            Color(android.graphics.Color.parseColor(colorHex))
                        } catch (e: Exception) {
                            MaterialTheme.colorScheme.primary
                        }

                        Surface(
                            onClick = { selectedColor = colorHex },
                            modifier = Modifier.aspectRatio(1f),
                            shape = MaterialTheme.shapes.small,
                            color = color,
                            border = if (selectedColor == colorHex)
                                ButtonDefaults.outlinedButtonBorder.copy(
                                    width = Dimens.SpacingXxs
                                ) else null
                        ) { }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onCreate(name, selectedColor) },
                enabled = name.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
