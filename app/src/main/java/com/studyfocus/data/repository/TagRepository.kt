package com.studyfocus.data.repository

import com.studyfocus.data.dao.TagDao
import com.studyfocus.data.model.Tag
import com.studyfocus.data.model.TaskTagCrossRef
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagRepository @Inject constructor(
    private val tagDao: TagDao
) {
    fun getAllTags(): Flow<List<Tag>> = tagDao.getAllTags()

    suspend fun insertTag(tag: Tag): Long = tagDao.insertTag(tag)

    suspend fun updateTag(tag: Tag) = tagDao.updateTag(tag)

    suspend fun deleteTag(tag: Tag) = tagDao.deleteTag(tag)

    suspend fun addTagToTask(taskId: Long, tagId: Long) =
        tagDao.insertTaskTagCrossRef(TaskTagCrossRef(taskId, tagId))

    suspend fun removeTagFromTask(taskId: Long, tagId: Long) =
        tagDao.deleteTaskTagCrossRef(TaskTagCrossRef(taskId, tagId))

    suspend fun clearTagsForTask(taskId: Long) = tagDao.deleteTagsForTask(taskId)
}
