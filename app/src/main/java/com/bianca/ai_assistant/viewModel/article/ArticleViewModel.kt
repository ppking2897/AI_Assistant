package com.bianca.ai_assistant.viewModel.article

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bianca.ai_assistant.infrastructure.room.article.ArticleEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val articleRepository: IArticleRepository,
) : ViewModel() {


    var articles by mutableStateOf<List<ArticleEntity>>(emptyList())
        private set

    private val _article = MutableStateFlow<ArticleEntity?>(null)
    val article: StateFlow<ArticleEntity?> = _article.asStateFlow()


    var articlesByTask by mutableStateOf<List<ArticleEntity>>(emptyList())
        private set

    val articlesFlow: StateFlow<List<ArticleEntity>> =
        articleRepository.getAllArticlesFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
            _article.value = articleRepository.getArticleById(id)
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

    fun insertArticle(article: ArticleEntity, onInserted: (ArticleEntity) -> Unit) {
        viewModelScope.launch {
            val id = articleRepository.insertArticle(article)
            val saved = article.copy(id = id)
            onInserted(saved)
        }
    }

    fun updateArticle(article: ArticleEntity, onFinish: (() -> Unit)? = null) {
        viewModelScope.launch {
            articleRepository.updateArticle(article)
            onFinish?.invoke()
            loadAllArticles()
        }
    }

    fun updateArticle(article: ArticleEntity, onUpdated: (ArticleEntity) -> Unit) {
        viewModelScope.launch {
            articleRepository.updateArticle(article)
            onUpdated(article)
        }
    }

    fun deleteArticle(article: ArticleEntity, onFinish: (() -> Unit)? = null) {
        viewModelScope.launch {
            articleRepository.deleteArticle(article)
            onFinish?.invoke()
            loadAllArticles()
        }
    }

    fun clearArticle() { _article.value = null }
}