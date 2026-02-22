package com.studyfocus.data.dao

import androidx.room.*
import com.studyfocus.data.model.PomodoroSession
import kotlinx.coroutines.flow.Flow

@Dao
interface PomodoroSessionDao {

    @Query("SELECT * FROM pomodoro_sessions WHERE taskId = :taskId ORDER BY startTime DESC")
    fun getSessionsByTask(taskId: Long): Flow<List<PomodoroSession>>

    @Query("SELECT * FROM pomodoro_sessions WHERE startTime >= :startOfDay AND startTime < :endOfDay ORDER BY startTime DESC")
    fun getSessionsByDate(startOfDay: Long, endOfDay: Long): Flow<List<PomodoroSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: PomodoroSession): Long

    @Update
    suspend fun updateSession(session: PomodoroSession)

    @Query("SELECT COUNT(*) FROM pomodoro_sessions WHERE isCompleted = 1 AND startTime >= :startOfDay AND startTime < :endOfDay")
    fun getCompletedSessionCountToday(startOfDay: Long, endOfDay: Long): Flow<Int>
}
