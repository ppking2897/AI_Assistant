package com.bianca.ai_assistant.viewModel.article

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bianca.ai_assistant.infrastructure.room.article.ArticleEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val articleRepository: ArticleRepository,
) : ViewModel() {


    var articles by mutableStateOf<List<ArticleEntity>>(emptyList())
        private set

    var article by mutableStateOf<ArticleEntity?>(null)
        private set


    var articlesByTask by mutableStateOf<List<ArticleEntity>>(emptyList())
        private set

    fun loadArticlesByTask(taskId: Long) {
        viewModelScope.launch {
            articlesByTask = articleRepository.getArticlesByTask(taskId)
        }
    }

    fun loadAllArticles() {
        viewModelScope.launch {
            articles = articleRepository.getAllArticles()
        }
    }

    fun loadArticleById(id: Long) {
        viewModelScope.launch {
            article = articleRepository.getArticleById(id)
        }
    }

    fun insertArticle(article: ArticleEntity, onFinish: (() -> Unit)? = null) {
        viewModelScope.launch {
            articleRepository.insertArticle(article)
            onFinish?.invoke()
            // 新增完可自動刷新列表
            loadAllArticles()
        }
    }

    fun updateArticle(article: ArticleEntity, onFinish: (() -> Unit)? = null) {
        viewModelScope.launch {
            articleRepository.updateArticle(article)
            onFinish?.invoke()
            loadAllArticles()
        }
    }

    fun deleteArticle(article: ArticleEntity, onFinish: (() -> Unit)? = null) {
        viewModelScope.launch {
            articleRepository.deleteArticle(article)
            onFinish?.invoke()
            loadAllArticles()
        }
    }

    fun clearArticle() {
        article = null
    }
}