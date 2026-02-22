package com.studyfocus.data.repository

import com.studyfocus.data.dao.ProjectDao
import com.studyfocus.data.model.Project
import com.studyfocus.data.model.ProjectWithTasks
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepository @Inject constructor(
    private val projectDao: ProjectDao
) {
    fun getAllProjects(): Flow<List<Project>> = projectDao.getAllProjects()

    fun getAllProjectsWithTasks(): Flow<List<ProjectWithTasks>> = projectDao.getAllProjectsWithTasks()

    fun getProjectWithTasks(projectId: Long): Flow<ProjectWithTasks?> =
        projectDao.getProjectWithTasks(projectId)

    suspend fun getProjectById(projectId: Long): Project? =
        projectDao.getProjectById(projectId)

    suspend fun insertProject(project: Project): Long = projectDao.insertProject(project)

    suspend fun updateProject(project: Project) = projectDao.updateProject(project)

    suspend fun deleteProject(project: Project) = projectDao.deleteProject(project)

    fun getActiveTaskCount(projectId: Long): Flow<Int> = projectDao.getActiveTaskCount(projectId)
}
