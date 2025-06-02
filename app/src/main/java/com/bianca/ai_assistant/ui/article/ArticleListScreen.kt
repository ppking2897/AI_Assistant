package com.bianca.ai_assistant.ui.article

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bianca.ai_assistant.infrastructure.room.article.ArticleEntity
import com.bianca.ai_assistant.infrastructure.room.task.TaskEntity
import com.bianca.ai_assistant.ui.dialog.ArticleEditDialog
import com.bianca.ai_assistant.ui.dialog.ConfirmDeleteDialog
import com.bianca.ai_assistant.ui.theme.AI_AssistantTheme
import com.bianca.ai_assistant.utils.ScrollBottomNavigation
import com.bianca.ai_assistant.viewModel.RecentActivityViewModel
import com.bianca.ai_assistant.viewModel.article.ArticleViewModel
import com.bianca.ai_assistant.viewModel.task.TaskViewModel


@ExperimentalMaterial3Api
@Composable
fun ArticleListScreenWithViewModel(
    viewModel: ArticleViewModel,
    taskViewModel: TaskViewModel,
    recentActivityViewModel: RecentActivityViewModel,
    onArticleClick: (ArticleEntity) -> Unit,
    onTaskClick: (TaskEntity) -> Unit,
    navController: NavHostController,
) {

    val tasks by taskViewModel.tasks.collectAsState()
    // 這是所有任務的 StateFlow<List<TaskEntity>>

    // 載入所有記事
    LaunchedEffect(Unit) { viewModel.loadAllArticles() }
    val articles = viewModel.articles


    var showEditDialog by remember { mutableStateOf(false) }
    var editingArticle by remember { mutableStateOf<ArticleEntity?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deletingArticle by remember { mutableStateOf<ArticleEntity?>(null) }

    ArticleListScreen(
        articles = articles,
        tasks = tasks,
        onArticleClick = onArticleClick,
        onTaskClick = onTaskClick,   // <--- 傳下去
        onAddArticle = {
//            editingArticle = null
//            showEditDialog = true
            navController.navigate("articleEdit")
        },
        onEditArticle = {
//            editingArticle = it
//            showEditDialog = true
            navController.navigate("articleEdit?articleId=${it.id}")
        },
        onDeleteArticle = {
            deletingArticle = it
            showDeleteDialog = true
        }
    )
    // 新增/編輯 Dialog
    if (showEditDialog) {
        ArticleEditDialog(
            article = editingArticle,
            onConfirm = { title, content, taskId ->
                if (editingArticle == null) {
                    // 新增
                    val newArticle = ArticleEntity(
                        title = title,
                        content = content,
                        taskId = taskId
                    )
//                    viewModel.insertArticle(newArticle) { showEditDialog = false }
                    recentActivityViewModel.recordArticleEvent("新增", newArticle)
                } else {
                    // 編輯
                    val updatedArticle = editingArticle!!.copy(
                        title = title,
                        content = content,
                        taskId = taskId
                    )
//                    viewModel.updateArticle(updatedArticle) { showEditDialog = false }
                    recentActivityViewModel.recordArticleEvent("編輯", updatedArticle)
                }
            },
            onCancel = { showEditDialog = false }
        )
    }

    // 刪除 Dialog
    if (showDeleteDialog && deletingArticle != null) {
        ConfirmDeleteDialog(
            articleTitle = deletingArticle!!.title,
            onConfirm = {
                viewModel.deleteArticle(deletingArticle!!) { showDeleteDialog = false }
                recentActivityViewModel.recordArticleEvent("刪除", deletingArticle!!)
            },
            onCancel = { showDeleteDialog = false }
        )
    }
}

@ExperimentalMaterial3Api
@Composable
fun ArticleListScreen(
    articles: List<ArticleEntity>,
    onArticleClick: (ArticleEntity) -> Unit,
    onAddArticle: () -> Unit,
    onTaskClick: (TaskEntity) -> Unit,  // <--- 新增
    onEditArticle: (ArticleEntity) -> Unit,
    onDeleteArticle: (ArticleEntity) -> Unit,
    tasks: List<TaskEntity>,
) {

    val listState = rememberLazyListState()

    ScrollBottomNavigation(listState)
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = { TopAppBar(title = { Text("記事清單") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddArticle) {
                Icon(Icons.Default.Add, contentDescription = "新增記事")
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(bottom =16.dp),
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .padding(padding)
                .navigationBarsPadding()
        ) {
            items(articles) { article ->
                ArticleListItem(
                    article = article,
                    tasks = tasks,
                    onTaskClick = onTaskClick,    // <--- 傳下去
                    onClick = { onArticleClick(article) },
                    onEdit = { onEditArticle(article) },
                    onDelete = { onDeleteArticle(article) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}

@Composable
fun ArticleListItem(
    article: ArticleEntity,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onTaskClick: (TaskEntity) -> Unit,
    onDelete: () -> Unit,
    tasks: List<TaskEntity>,
) {
    val relatedTask = tasks.find { it.id == article.taskId }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            Modifier.padding(12.dp)
        ) {
            Column(
                Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = article.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = article.content.take(40),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (relatedTask != null) {
                    Text(
                        text = "關聯任務：${relatedTask.title}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .clickable { onTaskClick(relatedTask) }
                    )
                }
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "編輯")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "刪除")
            }
        }
    }
}

@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
fun ArticleListScreenPreview() {
    AI_AssistantTheme {
        val sampleArticles = listOf(
            ArticleEntity(
                id = 1,
                title = "番茄鐘開發心得",
                content = "今天完成了番茄鐘核心邏輯設計，接下來準備串接AI推薦。",
                createdAt = System.currentTimeMillis(),
                taskId = 10L
            ),
            ArticleEntity(
                id = 2,
                title = "UI設計細節",
                content = "主色調選擇Material3風格，FilterChip自訂配色也完成了。",
                createdAt = System.currentTimeMillis(),
                taskId = null
            ),
            ArticleEntity(
                id = 3,
                title = "API串接注意事項",
                content = "記得設定權限，API KEY 請勿寫死在程式碼。",
                createdAt = System.currentTimeMillis(),
                taskId = 10L
            )
        )
        ArticleListScreen(
            articles = sampleArticles,
            onArticleClick = {},
            onAddArticle = {},
            onEditArticle = {},
            onDeleteArticle = {},
            tasks = listOf(),
            onTaskClick = { /* Do nothing in preview */ }
        )
    }
}

@ExperimentalMaterial3Api
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ArticleListScreenPreview_Night() {
    AI_AssistantTheme {
        val sampleArticles = listOf(
            ArticleEntity(
                id = 1,
                title = "番茄鐘開發心得",
                content = "今天完成了番茄鐘核心邏輯設計，接下來準備串接AI推薦。",
                createdAt = System.currentTimeMillis(),
                taskId = 10L
            ),
            ArticleEntity(
                id = 2,
                title = "UI設計細節",
                content = "主色調選擇Material3風格，FilterChip自訂配色也完成了。",
                createdAt = System.currentTimeMillis(),
                taskId = null
            ),
            ArticleEntity(
                id = 3,
                title = "API串接注意事項",
                content = "記得設定權限，API KEY 請勿寫死在程式碼。",
                createdAt = System.currentTimeMillis(),
                taskId = 10L
            )
        )
        ArticleListScreen(
            articles = sampleArticles,
            onArticleClick = {},
            onAddArticle = {},
            onEditArticle = {},
            onDeleteArticle = {},
            tasks = listOf(),
            onTaskClick = { /* Do nothing in preview */ }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ArticleListItemPreview() {
    ArticleListItem(
        article = ArticleEntity(
            id = 1,
            title = "AI助理設計紀錄",
            content = "採用Room+Hilt+Compose，體驗極佳！",
            createdAt = System.currentTimeMillis(),
            taskId = 10L
        ),
        onClick = {},
        onEdit = {},
        onDelete = {},
        tasks = listOf(),
        onTaskClick = { /* Do nothing in preview */ }
    )
}