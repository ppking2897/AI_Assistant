package com.bianca.ai_assistant.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bianca.ai_assistant.infrastructure.room.article.ArticleEntity

@Composable
fun ArticleEditDialog(
    article: ArticleEntity?,
    onConfirm: (title: String, content: String, taskId: Long?) -> Unit,
    onCancel: () -> Unit
) {
    // 可以複用你之前的 ArticleEditScreen，包裝在 AlertDialog 裡
    // 也可以直接用 Dialog 包裝 OutlinedTextField 等元件
    // 這裡給個 AlertDialog+Column範例
    var title by remember { mutableStateOf(article?.title ?: "") }
    var content by remember { mutableStateOf(article?.content ?: "") }

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(if (article == null) "新增記事" else "編輯記事") },
        text = {
            Column {

                // 你可補上任務選擇器
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("標題") }
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("內容") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(
                    // 這裡可以加入任務選擇欄位
                    title, content, null
                )
            }) { Text("儲存") }
        },
        dismissButton = {
            OutlinedButton(onClick = onCancel) { Text("取消") }
        }
    )
}