package com.studyfocus.ui.screens.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyfocus.data.model.*
import com.studyfocus.data.repository.ProjectRepository
import com.studyfocus.data.repository.TaskRepository
import com.studyfocus.data.repository.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskFormState(
    val editingTaskId: Long? = null,
    val title: String = "",
    val notes: String = "",
    val dueDate: Long? = null,
    val projectId: Long? = null,
    val pomodoroCount: Int = 2,
    val pomodoroDurationMinutes: Int = 25,
    val recurrenceType: RecurrenceType = RecurrenceType.NONE,
    val recurrenceDays: String = "",
    val subtaskTitles: List<String> = emptyList(),
    val selectedTagIds: List<Long> = emptyList(),
    val projects: List<Project> = emptyList(),
    val tags: List<Tag> = emptyList(),
    val isCompleted: Boolean = false,
    val completedPomodoros: Int = 0,
    val createdAt: Long = 0L
)

@HiltViewModel
class TaskCreateViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val projectRepository: ProjectRepository,
    private val tagRepository: TagRepository
) : ViewModel() {

    private val _formState = MutableStateFlow(TaskFormState())
    val formState: StateFlow<TaskFormState> = _formState.asStateFlow()

    init {
        viewModelScope.launch {
            projectRepository.getAllProjects().collect { projects ->
                _formState.update { it.copy(projects = projects) }
            }
        }
        viewModelScope.launch {
            tagRepository.getAllTags().collect { tags ->
                _formState.update { it.copy(tags = tags) }
            }
        }
    }

    fun loadTask(taskId: Long) {
        viewModelScope.launch {
            val taskWithDetails = taskRepository.getTaskById(taskId).firstOrNull()
            taskWithDetails?.let { detail ->
                val task = detail.task
                _formState.update {
                    it.copy(
                        editingTaskId = task.id,
                        title = task.title,
                        notes = task.notes,
                        dueDate = task.dueDate,
                        projectId = task.projectId,
                        pomodoroCount = task.pomodoroCount,
                        pomodoroDurationMinutes = task.pomodoroDurationMinutes,
                        recurrenceType = task.recurrenceType,
                        recurrenceDays = task.recurrenceDays,
                        isCompleted = task.isCompleted,
                        completedPomodoros = task.completedPomodoros,
                        createdAt = task.createdAt,
                        subtaskTitles = detail.subtasks.sortedBy { s -> s.position }.map { s -> s.title },
                        selectedTagIds = detail.tags.map { t -> t.id }
                    )
                }
            }
        }
    }

    fun updateTitle(title: String) { _formState.update { it.copy(title = title) } }
    fun updateNotes(notes: String) { _formState.update { it.copy(notes = notes) } }
    fun updateDueDate(date: Long?) { _formState.update { it.copy(dueDate = date) } }
    fun updateProject(projectId: Long?) { _formState.update { it.copy(projectId = projectId) } }
    fun updatePomodoroCount(count: Int) { _formState.update { it.copy(pomodoroCount = count.coerceIn(1, 20)) } }
    fun updatePomodoroDuration(minutes: Int) { _formState.update { it.copy(pomodoroDurationMinutes = minutes.coerceIn(5, 120)) } }
    fun updateRecurrenceType(type: RecurrenceType) { _formState.update { it.copy(recurrenceType = type) } }
    fun updateRecurrenceDays(days: String) { _formState.update { it.copy(recurrenceDays = days) } }

    fun addSubtask() {
        _formState.update { it.copy(subtaskTitles = it.subtaskTitles + "") }
    }

    fun updateSubtask(index: Int, title: String) {
        _formState.update {
            val list = it.subtaskTitles.toMutableList()
            if (index in list.indices) list[index] = title
            it.copy(subtaskTitles = list)
        }
    }

    fun removeSubtask(index: Int) {
        _formState.update {
            val list = it.subtaskTitles.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(subtaskTitles = list)
        }
    }

    fun toggleTag(tagId: Long) {
        _formState.update {
            val tags = if (tagId in it.selectedTagIds) it.selectedTagIds - tagId
            else it.selectedTagIds + tagId
            it.copy(selectedTagIds = tags)
        }
    }

    fun saveTask(onComplete: () -> Unit) {
        val state = _formState.value
        if (state.title.isBlank()) return

        viewModelScope.launch {
            val task = Task(
                id = state.editingTaskId ?: 0L,
                title = state.title,
                notes = state.notes,
                dueDate = state.dueDate,
                projectId = state.projectId,
                pomodoroCount = state.pomodoroCount,
                pomodoroDurationMinutes = state.pomodoroDurationMinutes,
                recurrenceType = state.recurrenceType,
                recurrenceDays = state.recurrenceDays,
                isCompleted = state.isCompleted,
                completedPomodoros = state.completedPomodoros,
                createdAt = if (state.createdAt > 0) state.createdAt else System.currentTimeMillis()
            )

            val taskId = if (state.editingTaskId != null) {
                taskRepository.updateTask(task)
                taskRepository.deleteSubtasksByTask(state.editingTaskId)
                tagRepository.clearTagsForTask(state.editingTaskId)
                state.editingTaskId
            } else {
                taskRepository.insertTask(task)
            }

            // Insert subtasks
            state.subtaskTitles.forEachIndexed { index, title ->
                if (title.isNotBlank()) {
                    taskRepository.insertSubtask(
                        Subtask(taskId = taskId, title = title, position = index)
                    )
                }
            }

            // Insert tag associations
            state.selectedTagIds.forEach { tagId ->
                tagRepository.addTagToTask(taskId, tagId)
            }

            onComplete()
        }
    }
}
