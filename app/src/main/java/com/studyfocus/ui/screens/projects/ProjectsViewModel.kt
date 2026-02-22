package com.studyfocus.ui.screens.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studyfocus.data.model.Project
import com.studyfocus.data.model.ProjectWithTasks
import com.studyfocus.data.model.Task
import com.studyfocus.data.model.TaskWithDetails
import com.studyfocus.data.repository.ProjectRepository
import com.studyfocus.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProjectsUiState(
    val projects: List<ProjectWithTasks> = emptyList(),
    val selectedProject: ProjectWithTasks? = null,
    val selectedProjectTasks: List<TaskWithDetails> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class ProjectsViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjectsUiState())
    val uiState: StateFlow<ProjectsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            projectRepository.getAllProjectsWithTasks().collect { projects ->
                _uiState.update {
                    it.copy(projects = projects, isLoading = false)
                }
            }
        }
    }

    fun selectProject(projectId: Long) {
        viewModelScope.launch {
            projectRepository.getProjectWithTasks(projectId).collect { project ->
                _uiState.update { it.copy(selectedProject = project) }
            }
        }
        viewModelScope.launch {
            taskRepository.getTasksByProject(projectId).collect { tasks ->
                _uiState.update { it.copy(selectedProjectTasks = tasks) }
            }
        }
    }

    fun createProject(name: String, colorHex: String) {
        viewModelScope.launch {
            projectRepository.insertProject(Project(name = name, colorHex = colorHex))
        }
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch {
            projectRepository.deleteProject(project)
        }
    }

    fun toggleTaskCompletion(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            taskRepository.setTaskCompleted(taskId, !isCompleted)
        }
    }
}
