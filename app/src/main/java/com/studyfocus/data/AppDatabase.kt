package com.studyfocus.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.studyfocus.data.dao.*
import com.studyfocus.data.model.*

@Database(
    entities = [
        Task::class,
        Subtask::class,
        Project::class,
        Tag::class,
        TaskTagCrossRef::class,
        PomodoroSession::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun subtaskDao(): SubtaskDao
    abstract fun projectDao(): ProjectDao
    abstract fun tagDao(): TagDao
    abstract fun pomodoroSessionDao(): PomodoroSessionDao
}
