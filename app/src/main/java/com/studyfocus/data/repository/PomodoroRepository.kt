package com.studyfocus.data.repository

import com.studyfocus.data.dao.PomodoroSessionDao
import com.studyfocus.data.model.PomodoroSession
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PomodoroRepository @Inject constructor(
    private val pomodoroSessionDao: PomodoroSessionDao
) {
    fun getSessionsByTask(taskId: Long): Flow<List<PomodoroSession>> =
        pomodoroSessionDao.getSessionsByTask(taskId)

    suspend fun insertSession(session: PomodoroSession): Long =
        pomodoroSessionDao.insertSession(session)

    suspend fun updateSession(session: PomodoroSession) =
        pomodoroSessionDao.updateSession(session)

    fun getCompletedTodayCount(): Flow<Int> {
        val today = LocalDate.now()
        val startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return pomodoroSessionDao.getCompletedSessionCountToday(startOfDay, endOfDay)
    }
}
