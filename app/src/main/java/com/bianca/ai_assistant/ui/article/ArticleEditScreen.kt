package com.bianca.ai_assistant.ui.article

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bianca.ai_assistant.infrastructure.room.article.ArticleEntity
import com.bianca.ai_assistant.infrastructure.room.task.TaskEntity
import com.bianca.ai_assistant.ui.theme.AI_AssistantTheme
import com.bianca.ai_assistant.viewModel.article.ArticleViewModel


@Composable
fun ArticleEditScreenWithViewModel(
    viewModel: ArticleViewModel,
    articleId: Long? = null,
    initialTaskId: Long? = null,
    allTasks: List<TaskEntity>,
    onSaveSuccess: () -> Unit,
    onCancel: () -> Unit
) {
    // 1. 載入單筆資料（僅編輯模式時）
    LaunchedEffect(articleId) {
        if (articleId != null) {
            viewModel.loadArticleById(articleId)
        } else {
            viewModel.clearArticle()// 清空，避免舊資料殘留
        }
    }

    val article = viewModel.article

    // 假設 article 為 null 則為新增，否則為編輯
    ArticleEditScreen(
        article = article,
        allTasks = allTasks,
        initialTaskId = initialTaskId,
        onSave = { title, content, taskId ->
            if (article == null) {
                viewModel.insertArticle(
                    ArticleEntity(
                        title = title,
                        content = content,
                        taskId = taskId
                        // 其他欄位預設
                    )
                )
            } else {
                viewModel.updateArticle(
                    article.copy(
                        title = title,
                        content = content,
                        taskId = taskId
                    )
                )
            }
            onSaveSuccess()
        },
        onCancel = onCancel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleEditScreen(
    article: ArticleEntity? = null,
    allTasks: List<TaskEntity>,
    initialTaskId: Long? = null, // 預設關聯任務（通常任務詳情頁點進來時給）
    onSave: (title: String, content: String, taskId: Long?) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf(article?.title ?: "") }
    var content by remember { mutableStateOf(article?.content ?: "") }
    var selectedTaskId by remember { mutableStateOf(article?.taskId ?: initialTaskId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (article == null) "新增記事" else "編輯記事") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                textStyle = TextStyle(fontSize = MaterialTheme.typography.bodyLarge.fontSize),
                onValueChange = { title = it },
                label = { Text("標題") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = content,
                textStyle = TextStyle(fontSize = MaterialTheme.typography.bodyLarge.fontSize),
                onValueChange = { content = it },
                label = { Text("內容") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            // 任務選擇器
            TaskDropdownSelector(
                tasks = allTasks,
                selectedTaskId = selectedTaskId,
                onTaskSelected = { selectedTaskId = it }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Button(
                    onClick = { onSave(title, content, selectedTaskId) },
                    enabled = title.isNotBlank()
                ) {
                    Text("儲存")
                }
                Spacer(modifier = Modifier.width(12.dp))
                OutlinedButton(onClick = onCancel) { Text("取消") }
            }
        }
    }
}

@Composable
fun TaskDropdownSelector(
    tasks: List<TaskEntity>,
    selectedTaskId: Long?,
    onTaskSelected: (Long?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedTask = tasks.find { it.id == selectedTaskId }
    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selectedTask?.title ?: "不關聯任務")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("不關聯任務") },
                onClick = {
                    onTaskSelected(null)
                    expanded = false
                }
            )
            tasks.forEach { task ->
                DropdownMenuItem(
                    text = { Text(task.title) },
                    onClick = {
                        onTaskSelected(task.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "新增記事")
@Composable
fun PreviewArticleEditScreen_Add() {
    AI_AssistantTheme {
        ArticleEditScreen(
            article = null,
            allTasks = listOf(
                TaskEntity(id = 1, title = "Android專案", description = "主線任務"),
                TaskEntity(id = 2, title = "測試UI", description = "子任務")
            ),
            onSave = { _, _, _ -> },
            onCancel = {}
        )
    }
}

@Preview(showBackground = true, name = "新增記事", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewArticleEditScreen_Add_Night() {
    AI_AssistantTheme {
        ArticleEditScreen(
            article = null,
            allTasks = listOf(
                TaskEntity(id = 1, title = "Android專案", description = "主線任務"),
                TaskEntity(id = 2, title = "測試UI", description = "子任務")
            ),
            onSave = { _, _, _ -> },
            onCancel = {}
        )
    }
}

@Preview(showBackground = true, name = "編輯記事")
@Composable
fun PreviewArticleEditScreen_Edit() {
    AI_AssistantTheme {
        ArticleEditScreen(
            article = ArticleEntity(
                id = 100,
                title = "紀錄今天的進度",
                content = "完成了Compose任務列表UI與資料串接。",
                createdAt = System.currentTimeMillis(),
                taskId = 1L
            ),
            allTasks = listOf(
                TaskEntity(id = 1, title = "Android專案", description = "主線任務"),
                TaskEntity(id = 2, title = "測試UI", description = "子任務")
            ),
            onSave = { _, _, _ -> },
            onCancel = {}
        )
    }
}

@Preview(showBackground = true, name = "編輯記事", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewArticleEditScreen_Edit_Night() {
    AI_AssistantTheme {
        ArticleEditScreen(
            article = ArticleEntity(
                id = 100,
                title = "紀錄今天的進度",
                content = "完成了Compose任務列表UI與資料串接。",
                createdAt = System.currentTimeMillis(),
                taskId = 1L
            ),
            allTasks = listOf(
                TaskEntity(id = 1, title = "Android專案", description = "主線任務"),
                TaskEntity(id = 2, title = "測試UI", description = "子任務")
            ),
            onSave = { _, _, _ -> },
            onCancel = {}
        )
    }
}