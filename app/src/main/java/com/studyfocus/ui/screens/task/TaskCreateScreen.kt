package com.studyfocus.ui.screens.task

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studyfocus.data.model.RecurrenceType
import com.studyfocus.ui.theme.Dimens
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCreateScreen(
    projectId: Long? = null,
    onNavigateBack: () -> Unit,
    viewModel: TaskCreateViewModel = hiltViewModel()
) {
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(projectId) {
        if (projectId != null && projectId > 0) {
            viewModel.updateProject(projectId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Task", style = MaterialTheme.typography.headlineSmall) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.saveTask(onNavigateBack) },
                        enabled = formState.title.isNotBlank()
                    ) {
                        Text("Save", style = MaterialTheme.typography.titleMedium)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Dimens.SpacingLg),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)
        ) {
            // Title
            item {
                OutlinedTextField(
                    value = formState.title,
                    onValueChange = viewModel::updateTitle,
                    placeholder = { Text("Task name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.titleLarge,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
            }

            // Notes
            item {
                OutlinedTextField(
                    value = formState.notes,
                    onValueChange = viewModel::updateNotes,
                    placeholder = { Text("Notes") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 80.dp),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    minLines = 3
                )
            }

            // Due Date
            item {
                val dateText = formState.dueDate?.let {
                    Instant.ofEpochMilli(it)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
                } ?: "No due date"

                OutlinedCard(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(Dimens.SpacingLg),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CalendarToday, "Due date",
                            tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(Dimens.SpacingMd))
                        Text(dateText, style = MaterialTheme.typography.bodyLarge)
                        if (formState.dueDate != null) {
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(onClick = { viewModel.updateDueDate(null) }) {
                                Icon(Icons.Default.Close, "Clear date",
                                    modifier = Modifier.size(Dimens.IconSm))
                            }
                        }
                    }
                }
            }

            // Project selector
            item {
                var expanded by remember { mutableStateOf(false) }
                val selectedProject = formState.projects.find { it.id == formState.projectId }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedProject?.name ?: "No project",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        label = { Text("Project") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            text = { Text("No project") },
                            onClick = {
                                viewModel.updateProject(null)
                                expanded = false
                            }
                        )
                        formState.projects.forEach { project ->
                            DropdownMenuItem(
                                text = { Text(project.name) },
                                onClick = {
                                    viewModel.updateProject(project.id)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Pomodoro settings
            item {
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(Dimens.SpacingLg)) {
                        Text("Pomodoro", style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(Dimens.SpacingMd))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Sessions", style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f))
                            IconButton(onClick = { viewModel.updatePomodoroCount(formState.pomodoroCount - 1) }) {
                                Icon(Icons.Default.Remove, "Decrease")
                            }
                            Text("${formState.pomodoroCount}",
                                style = MaterialTheme.typography.titleMedium)
                            IconButton(onClick = { viewModel.updatePomodoroCount(formState.pomodoroCount + 1) }) {
                                Icon(Icons.Default.Add, "Increase")
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Duration (min)", style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f))
                            IconButton(onClick = { viewModel.updatePomodoroDuration(formState.pomodoroDurationMinutes - 5) }) {
                                Icon(Icons.Default.Remove, "Decrease")
                            }
                            Text("${formState.pomodoroDurationMinutes}",
                                style = MaterialTheme.typography.titleMedium)
                            IconButton(onClick = { viewModel.updatePomodoroDuration(formState.pomodoroDurationMinutes + 5) }) {
                                Icon(Icons.Default.Add, "Increase")
                            }
                        }
                    }
                }
            }

            // Recurrence
            item {
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(Dimens.SpacingLg)) {
                        Text("Repeat", style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(Dimens.SpacingSm))

                        val options = RecurrenceType.entries
                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                            options.forEachIndexed { index, type ->
                                SegmentedButton(
                                    selected = formState.recurrenceType == type,
                                    onClick = { viewModel.updateRecurrenceType(type) },
                                    shape = SegmentedButtonDefaults.itemShape(index, options.size)
                                ) {
                                    Text(
                                        when (type) {
                                            RecurrenceType.NONE -> "None"
                                            RecurrenceType.DAILY -> "Daily"
                                            RecurrenceType.WEEKLY -> "Weekly"
                                            RecurrenceType.CUSTOM -> "Custom"
                                        },
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                        }

                        // Day picker for custom
                        if (formState.recurrenceType == RecurrenceType.CUSTOM) {
                            Spacer(modifier = Modifier.height(Dimens.SpacingSm))
                            val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                            val selectedDays = formState.recurrenceDays.split(",")
                                .filter { it.isNotBlank() }
                                .map { it.toIntOrNull() ?: 0 }
                                .toSet()

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                days.forEachIndexed { index, day ->
                                    val dayIndex = index + 1
                                    FilterChip(
                                        selected = dayIndex in selectedDays,
                                        onClick = {
                                            val newDays = if (dayIndex in selectedDays)
                                                selectedDays - dayIndex else selectedDays + dayIndex
                                            viewModel.updateRecurrenceDays(
                                                newDays.sorted().joinToString(",")
                                            )
                                        },
                                        label = { Text(day, style = MaterialTheme.typography.labelSmall) },
                                        modifier = Modifier.height(32.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Subtasks
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Subtasks", style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f))
                    IconButton(onClick = viewModel::addSubtask) {
                        Icon(Icons.Default.Add, "Add subtask",
                            tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            itemsIndexed(formState.subtaskTitles) { index, title ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { viewModel.updateSubtask(index, it) },
                        placeholder = { Text("Subtask ${index + 1}") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        textStyle = MaterialTheme.typography.bodyMedium,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                    IconButton(onClick = { viewModel.removeSubtask(index) }) {
                        Icon(Icons.Default.Close, "Remove",
                            modifier = Modifier.size(Dimens.IconSm))
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(Dimens.SpacingMassive)) }
        }
    }

    // Date picker dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = formState.dueDate ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateDueDate(datePickerState.selectedDateMillis)
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
