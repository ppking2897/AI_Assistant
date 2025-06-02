package com.bianca.ai_assistant.viewModel

import com.bianca.ai_assistant.infrastructure.room.RecentActivityDao
import com.bianca.ai_assistant.infrastructure.room.RecentActivityEntity
import com.bianca.ai_assistant.infrastructure.room.article.ArticleEntity
import com.bianca.ai_assistant.infrastructure.room.task.TaskEntity
import kotlinx.coroutines.flow.Flow

interface IRecentActivityRepository {
    fun getAllRecentActivities(): Flow<List<RecentActivityEntity>>
    suspend fun recordTaskEvent(action: String, task: TaskEntity)
    suspend fun recordArticleEvent(action: String, article: ArticleEntity)
    suspend fun recordAiEvent(question: String, answer: String?)
}

class RecentActivityRepository(
    private val dao: RecentActivityDao
) : IRecentActivityRepository {

    override fun getAllRecentActivities(): Flow<List<RecentActivityEntity>> =
        dao.getAllRecentActivities()

    override suspend fun recordTaskEvent(action: String, task: TaskEntity) {
        dao.insert(
            RecentActivityEntity(
                type = "TASK",
                action = action,
                refId = task.id,
                title = task.title,
                summary = task.description,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    override suspend fun recordArticleEvent(action: String, article: ArticleEntity) {
        dao.insert(
            RecentActivityEntity(
                type = "ARTICLE",
                action = action,
                refId = article.id,
                title = article.title,
                summary = article.content?.take(20), // 只取前20字當摘要
                timestamp = System.currentTimeMillis()
            )
        )
    }

    override suspend fun recordAiEvent(question: String, answer: String?) {
        dao.insert(
            RecentActivityEntity(
                type = "AI",
                action = "ASK",
                refId = null,
                title = question,
                summary = answer?.take(20), // 只取前20字當摘要
                timestamp = System.currentTimeMillis()
            )
        )
    }
}