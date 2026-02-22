package com.studyfocus.data.repository

import com.studyfocus.data.dao.TaskDao
import com.studyfocus.data.dao.SubtaskDao
import com.studyfocus.data.model.Task
import com.studyfocus.data.model.Subtask
import com.studyfocus.data.model.TaskWithDetails
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao,
    private val subtaskDao: SubtaskDao
) {
    fun getAllTasks(): Flow<List<TaskWithDetails>> = taskDao.getAllTasksWithDetails()

    fun getTasksByProject(projectId: Long): Flow<List<TaskWithDetails>> =
        taskDao.getTasksByProject(projectId)

    fun getTodayTasks(): Flow<List<TaskWithDetails>> {
        val today = LocalDate.now()
        val startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return taskDao.getTasksDueToday(startOfDay, endOfDay)
    }

    fun getRecentTasks(limit: Int = 10): Flow<List<TaskWithDetails>> =
        taskDao.getRecentTasks(limit)

    fun getTaskById(taskId: Long): Flow<TaskWithDetails?> =
        taskDao.getTaskById(taskId)

    suspend fun getTaskByIdOnce(taskId: Long): Task? =
        taskDao.getTaskByIdOnce(taskId)

    suspend fun insertTask(task: Task): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    suspend fun setTaskCompleted(taskId: Long, isCompleted: Boolean) =
        taskDao.setTaskCompleted(taskId, isCompleted)

    suspend fun updateCompletedPomodoros(taskId: Long, completed: Int) =
        taskDao.updateCompletedPomodoros(taskId, completed)

    // Subtask operations
    fun getSubtasks(taskId: Long): Flow<List<Subtask>> =
        subtaskDao.getSubtasksByTask(taskId)

    suspend fun insertSubtask(subtask: Subtask): Long = subtaskDao.insertSubtask(subtask)

    suspend fun updateSubtask(subtask: Subtask) = subtaskDao.updateSubtask(subtask)

    suspend fun deleteSubtask(subtask: Subtask) = subtaskDao.deleteSubtask(subtask)
    
    suspend fun deleteSubtasksByTask(taskId: Long) = subtaskDao.deleteSubtasksByTask(taskId)

    suspend fun setSubtaskCompleted(subtaskId: Long, isCompleted: Boolean) =
        subtaskDao.setSubtaskCompleted(subtaskId, isCompleted)
}
