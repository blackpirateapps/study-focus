package com.studyfocus.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class RecurrenceType {
    NONE, DAILY, WEEKLY, CUSTOM
}

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Project::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("projectId"), Index("dueDate")]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val notes: String = "",
    val dueDate: Long? = null,
    val isCompleted: Boolean = false,
    val projectId: Long? = null,
    val pomodoroCount: Int = 2,
    val pomodoroDurationMinutes: Int = 25,
    val completedPomodoros: Int = 0,
    val recurrenceType: RecurrenceType = RecurrenceType.NONE,
    val recurrenceDays: String = "", // comma-separated day indices (1=MON, 7=SUN)
    val createdAt: Long = System.currentTimeMillis()
)
