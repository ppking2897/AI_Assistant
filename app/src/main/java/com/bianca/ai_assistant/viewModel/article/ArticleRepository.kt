package com.bianca.ai_assistant.viewModel.article

import com.bianca.ai_assistant.infrastructure.room.article.ArticleDao
import com.bianca.ai_assistant.infrastructure.room.article.ArticleEntity
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow


interface ArticleRepository {
    suspend fun getAllArticles(): List<ArticleEntity>
    suspend fun getArticlesByTask(taskId: Long): List<ArticleEntity>
    suspend fun insertArticle(article: ArticleEntity): Long
    suspend fun updateArticle(article: ArticleEntity)
    suspend fun deleteArticle(article: ArticleEntity)
    suspend fun getArticleById(id: Long): ArticleEntity?
}

class ArticleRepositoryImpl @Inject constructor(
    private val articleDao: ArticleDao
) : ArticleRepository {

    override suspend fun getAllArticles(): List<ArticleEntity> =
        articleDao.getAllArticles()

    override suspend fun getArticlesByTask(taskId: Long): List<ArticleEntity> =
        articleDao.getArticlesByTask(taskId)

    override suspend fun insertArticle(article: ArticleEntity): Long =
        articleDao.insert(article)

    override suspend fun updateArticle(article: ArticleEntity) =
        articleDao.update(article)

    override suspend fun deleteArticle(article: ArticleEntity) =
        articleDao.delete(article)

    override suspend fun getArticleById(id: Long): ArticleEntity? =
        articleDao.getArticleById(id)

}