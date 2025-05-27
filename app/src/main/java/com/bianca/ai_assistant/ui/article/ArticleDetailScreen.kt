package com.bianca.ai_assistant.ui.article

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bianca.ai_assistant.infrastructure.room.article.ArticleEntity
import com.bianca.ai_assistant.infrastructure.room.task.TaskEntity
import com.bianca.ai_assistant.utils.formatDateTime
import com.bianca.ai_assistant.viewModel.article.ArticleViewModel


@ExperimentalMaterial3Api
@Composable
fun ArticleDetailScreenWithViewModel(
    viewModel: ArticleViewModel,
    articleId: Long,
    getTaskById: (Long) -> TaskEntity?,
    onTaskClick: ((TaskEntity) -> Unit)? = null,
    onBack: () -> Unit,
) {
    // 頁面進來或 articleId 變時觸發查詢
    LaunchedEffect(articleId) {
        viewModel.loadArticleById(articleId)
    }

    val article = viewModel.article

    if (article != null) {
        val relatedTask = article.taskId?.let { getTaskById(it) }
        ArticleDetailScreen(
            article = article,
            relatedTask = relatedTask,
            onTaskClick = { relatedTask?.let { onTaskClick?.invoke(it) } },
            onBack = onBack
        )
    } else {
        Text("載入中...")
    }
}

@ExperimentalMaterial3Api
@Composable
fun ArticleDetailScreen(
    article: ArticleEntity,
    relatedTask: TaskEntity?,
    onTaskClick: (() -> Unit)? = null,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("記事詳情") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(article.title, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(article.content, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text("建立於 ${formatDateTime(article.createdAt)}")
            if (relatedTask != null && onTaskClick != null) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = onTaskClick) {
                    Text("查看關聯任務：${relatedTask.title}")
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ArticleDetailScreenPreview() {
    val article = ArticleEntity(
        id = 1,
        title = "專案進度紀錄",
        content = "今天完成了 Room + Compose 詳情頁的 UI 開發與測試。",
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis(),
        taskId = 10L
    )
    val relatedTask = TaskEntity(
        id = 10,
        title = "AI助理專案開發",
        description = "負責核心功能設計",
        isDone = false
    )

    ArticleDetailScreen(
        article = article,
        relatedTask = relatedTask,
        onTaskClick = { /* 預覽不做事 */ },
        onBack = { /* 預覽不做事 */ }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "無關聯任務")
@Composable
fun ArticleDetailScreenPreview_NoTask() {
    val article = ArticleEntity(
        id = 2,
        title = "單純記事內容",
        content = "這是一篇沒有關聯任務的記事。",
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis(),
        taskId = null
    )

    ArticleDetailScreen(
        article = article,
        relatedTask = null,
        onTaskClick = null,
        onBack = {}
    )
}
