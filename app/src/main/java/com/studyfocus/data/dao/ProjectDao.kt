package com.studyfocus.data.dao

import androidx.room.*
import com.studyfocus.data.model.Project
import com.studyfocus.data.model.ProjectWithTasks
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {

    @Query("SELECT * FROM projects ORDER BY createdAt DESC")
    fun getAllProjects(): Flow<List<Project>>

    @Transaction
    @Query("SELECT * FROM projects ORDER BY createdAt DESC")
    fun getAllProjectsWithTasks(): Flow<List<ProjectWithTasks>>

    @Transaction
    @Query("SELECT * FROM projects WHERE id = :projectId")
    fun getProjectWithTasks(projectId: Long): Flow<ProjectWithTasks?>

    @Query("SELECT * FROM projects WHERE id = :projectId")
    suspend fun getProjectById(projectId: Long): Project?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project): Long

    @Update
    suspend fun updateProject(project: Project)

    @Delete
    suspend fun deleteProject(project: Project)

    @Query("SELECT COUNT(*) FROM tasks WHERE projectId = :projectId AND isCompleted = 0")
    fun getActiveTaskCount(projectId: Long): Flow<Int>
}
