package com.studyfocus.data.dao

import androidx.room.*
import com.studyfocus.data.model.Task
import com.studyfocus.data.model.TaskWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Transaction
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasksWithDetails(): Flow<List<TaskWithDetails>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE projectId = :projectId ORDER BY isCompleted ASC, createdAt DESC")
    fun getTasksByProject(projectId: Long): Flow<List<TaskWithDetails>>

    @Transaction
    @Query("""
        SELECT * FROM tasks 
        WHERE dueDate IS NOT NULL 
        AND dueDate >= :startOfDay 
        AND dueDate < :endOfDay 
        ORDER BY isCompleted ASC, dueDate ASC
    """)
    fun getTasksDueToday(startOfDay: Long, endOfDay: Long): Flow<List<TaskWithDetails>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentTasks(limit: Int = 10): Flow<List<TaskWithDetails>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: Long): Flow<TaskWithDetails?>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskByIdOnce(taskId: Long): Task?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE id = :taskId")
    suspend fun setTaskCompleted(taskId: Long, isCompleted: Boolean)

    @Query("UPDATE tasks SET completedPomodoros = :completed WHERE id = :taskId")
    suspend fun updateCompletedPomodoros(taskId: Long, completed: Int)
}
