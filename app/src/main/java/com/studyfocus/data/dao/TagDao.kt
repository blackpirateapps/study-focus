package com.studyfocus.data.dao

import androidx.room.*
import com.studyfocus.data.model.Tag
import com.studyfocus.data.model.TaskTagCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {

    @Query("SELECT * FROM tags ORDER BY name ASC")
    fun getAllTags(): Flow<List<Tag>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: Tag): Long

    @Update
    suspend fun updateTag(tag: Tag)

    @Delete
    suspend fun deleteTag(tag: Tag)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTaskTagCrossRef(crossRef: TaskTagCrossRef)

    @Delete
    suspend fun deleteTaskTagCrossRef(crossRef: TaskTagCrossRef)

    @Query("DELETE FROM task_tag_cross_ref WHERE taskId = :taskId")
    suspend fun deleteTagsForTask(taskId: Long)
}
