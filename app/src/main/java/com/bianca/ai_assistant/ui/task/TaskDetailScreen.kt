package com.bianca.ai_assistant.ui.task

import androidx.compose.foundation.background
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
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bianca.ai_assistant.infrastructure.room.article.ArticleEntity
import com.bianca.ai_assistant.infrastructure.room.task.TaskEntity
import com.bianca.ai_assistant.ui.theme.AI_AssistantTheme
import com.bianca.ai_assistant.utils.formatDueTimeWithDateOption
import com.bianca.ai_assistant.viewModel.article.ArticleViewModel


@ExperimentalMaterial3Api
@Composable
fun TaskDetailScreenWithViewModel(
    task: TaskEntity,
    articleViewModel: ArticleViewModel,
    onArticleClick: (ArticleEntity) -> Unit,
    onAddArticle: () -> Unit,
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
    onAddArticle: () -> Unit,
) {

    Scaffold(
        topBar = { TopAppBar(title = { Text(task.title) }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("新增記事") },
                onClick = onAddArticle
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(start = 16.dp, end = 16.dp, bottom = 10.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            // ...任務資訊顯示...
            HorizontalDivider()
            Text(
                text = task.description ?: "",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 10.dp)
            )

            Spacer(modifier = Modifier.height(60.dp))

            task.dueTime?.let {
                Text(
                    text = "提醒時間: ${formatDueTimeWithDateOption(it)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (it < System.currentTimeMillis()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "相關記事", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))
            if (articles.isEmpty()) {
                Text(
                    "尚無相關記事",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(8.dp)
                )
            } else {
                articles.forEach { article ->
                    Card(
                        colors = CardColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContentColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp , horizontal = 8.dp)
                            .clickable { onArticleClick(article) }
                    ) {
                        Column(
                            Modifier.padding(
                                start = 16.dp, end = 16.dp, top = 10.dp, bottom = 10.dp
                            )
                        ) {
                            Text(article.title, style = MaterialTheme.typography.bodyLarge)
                            Text(
                                article.content.take(32),
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "任務詳情頁 Preview")
@Composable
fun TaskDetailScreenPreview() {
    AI_AssistantTheme {
        val task = TaskEntity(
            id = 1,
            title = "專案規劃與開發",
            description = "規劃新功能並分配人員。",
            isDone = false,
            dueTime = 55555555555L, // 假設的到期時間
        )

        val articles = listOf(
            ArticleEntity(
                id = 101,
                title = "會議紀錄",
                content = "今天開會討論了分工細節與開發時程，需持續追蹤進度。",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                taskId = task.id
            ),
            ArticleEntity(
                id = 102,
                title = "功能驗證",
                content = "初步驗證功能正常，UI 尚有細節需調整。",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                taskId = task.id
            )
        )

        TaskDetailScreen(
            task = task,
            articles = articles,
            onArticleClick = {},
            onAddArticle = {}
        )
    }
}