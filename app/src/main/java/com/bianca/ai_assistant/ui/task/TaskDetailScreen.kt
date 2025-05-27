package com.bianca.ai_assistant.ui.task

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bianca.ai_assistant.infrastructure.room.article.ArticleEntity
import com.bianca.ai_assistant.infrastructure.room.task.TaskEntity
import com.bianca.ai_assistant.viewModel.article.ArticleViewModel


@ExperimentalMaterial3Api
@Composable
fun TaskDetailScreenWithViewModel(
    task: TaskEntity,
    articleViewModel: ArticleViewModel,
    onArticleClick: (ArticleEntity) -> Unit,
    onAddArticle: () -> Unit
) {
    LaunchedEffect(task.id) {
        articleViewModel.loadArticlesByTask(task.id)
    }

    val articles = articleViewModel.articlesByTask

    TaskDetailScreen(
        task = task,
        articles = articles,
        onArticleClick = onArticleClick,
        onAddArticle = onAddArticle
    )
}

@ExperimentalMaterial3Api
@Composable
fun TaskDetailScreen(
    task: TaskEntity,
    articles: List<ArticleEntity>,
    onArticleClick: (ArticleEntity) -> Unit,
    onAddArticle: () -> Unit
) {

    Scaffold(
        topBar = { TopAppBar(title = { Text(task.title) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddArticle) {
                Icon(Icons.Default.Add, contentDescription = "新增關聯記事")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            // ...任務資訊顯示...

            Text("相關記事", style = MaterialTheme.typography.titleMedium)
            if (articles.isEmpty()) {
                Text("尚無相關記事", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(8.dp))
            } else {
                articles.forEach { article ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onArticleClick(article) }
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(article.title, style = MaterialTheme.typography.bodyLarge)
                            Text(article.content.take(32), style = MaterialTheme.typography.bodySmall, maxLines = 1)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
