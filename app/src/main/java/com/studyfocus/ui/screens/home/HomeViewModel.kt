package com.studyfocus.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyfocus.data.model.ProjectWithTasks
import com.studyfocus.data.model.TaskWithDetails
import com.studyfocus.data.repository.ProjectRepository
import com.studyfocus.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val recentTasks: List<TaskWithDetails> = emptyList(),
    val projects: List<ProjectWithTasks> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                taskRepository.getRecentTasks(10),
                projectRepository.getAllProjectsWithTasks()
            ) { tasks, projects ->
                HomeUiState(
                    recentTasks = tasks,
                    projects = projects,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun toggleTaskCompletion(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            taskRepository.setTaskCompleted(taskId, !isCompleted)
        }
    }
}
