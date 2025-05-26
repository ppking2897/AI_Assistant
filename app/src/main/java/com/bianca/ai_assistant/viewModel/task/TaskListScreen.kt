package com.bianca.ai_assistant.viewModel.task

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bianca.ai_assistant.infrastructure.TaskEntity
import com.bianca.ai_assistant.infrastructure.alarm.ScheduleAlarmWithPermissionCheck
import com.bianca.ai_assistant.ui.theme.AI_AssistantTheme
import com.bianca.ai_assistant.viewModel.dialog.TaskEditDialog

/**
 * 📝 備註與延伸建議
 * 新增/編輯任務可以用 Dialog 或新分頁
 *
 * Remind/AlarmManager 可再後續結合
 *
 * 支援搜尋/篩選可進一步設計
 *
 * TaskRepository 可以同時支援本地（Room）與遠端（雲端/同步）
 *
 *
 */

@ExperimentalMaterial3Api
@Composable
fun TaskListScreenWithViewModel(viewModel: TaskViewModel) {
    // 觀察 StateFlow
    val tasks by viewModel.tasks.collectAsState()

    // 可以依需求新增 showDialog/editingTask 等 UI 狀態
    var showDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<TaskEntity?>(null) }

    // 將 ViewModel 的操作方法傳給 stateless UI
    TaskListScreen(
        tasks = tasks,
        onAddTask = { task ->
            viewModel.addTask(task)
        },
        onEditTask = { task ->
            editingTask = task
            showDialog = true
        },
        onToggleTask = { task ->
            viewModel.toggleTask(task)
        },
        onDeleteTask = { task ->
            viewModel.deleteTask(task)
        }
    )

    // 彈出 Dialog（可根據需要顯示新增/編輯Dialog）
    if (showDialog) {
        TaskEditDialog(
            initialTask = editingTask,
            onDismiss = { showDialog = false },
            onConfirm = { updatedTask ->
                viewModel.updateTask(updatedTask)
                showDialog = false
            }
        )
    }
}

@ExperimentalMaterial3Api
@Composable
fun TaskListScreen(
    tasks: List<TaskEntity>,
    onAddTask: (TaskEntity) -> Unit = {},
    onEditTask: (TaskEntity) -> Unit = {},
    onToggleTask: (TaskEntity) -> Unit = {},
    onDeleteTask: (TaskEntity) -> Unit = {},
) {
    var showDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<TaskEntity?>(null) }
    var alarmInfo by remember { mutableStateOf<Triple<Long, Long, String>?>(null) } // (taskId, dueTime, title)

    // 新增/編輯按鈕事件
    val onDialogConfirm: (TaskEntity) -> Unit = { task ->
        if (editingTask == null) onAddTask(task) else onEditTask(task)
        // 判斷有無提醒時間
        if (task.dueTime != null) {
            alarmInfo = Triple(task.id, task.dueTime!!, task.title)
        }
        showDialog = false
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingTask = null
                showDialog = true
            }) { Icon(Icons.Default.Add, contentDescription = "新增") }
        }
    ) { innerPadding ->
        LazyColumn(contentPadding = innerPadding) {
            items(tasks) { task ->
                TaskItemRow(
                    task = task,
                    onClick = { onEditTask(task) },
                    onToggle = { onToggleTask(task) },
                    onDelete = { onDeleteTask(task) }
                )
            }
        }
        if (showDialog) {
            TaskEditDialog(
                initialTask = editingTask,
                onDismiss = { showDialog = false },
                onConfirm = onDialogConfirm
            )
        }
        // 排提醒，觸發時自動消失
        alarmInfo?.let { (taskId, dueTime, title) ->
            ScheduleAlarmWithPermissionCheck(
                taskId = taskId,
                timeMillis = dueTime,
                title = title,
                desc = "",
                onAlarmScheduled = { alarmInfo = null }
            )
        }
    }
}

@Composable
fun TaskItemRow(
    task: TaskEntity,
    onClick: () -> Unit = {},
    onToggle: () -> Unit = {},
    onDelete: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isDone,
            onCheckedChange = { onToggle() }
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                textDecoration = if (task.isDone) TextDecoration.LineThrough else null

            )
            if (!task.description.isNullOrBlank()) {
                Text(
                    text = task.description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp),
                    textDecoration = if (task.isDone) TextDecoration.LineThrough else null
                )
            }
        }
        IconButton(onClick = { onDelete() }) {
            Icon(Icons.Default.Delete, contentDescription = "刪除")
        }
    }
}

@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
fun PreviewTaskListScreen() {
    AI_AssistantTheme {
        val fakeTasks = listOf(
            TaskEntity(title = "會議", description = "9:00 記得帶文件", isDone = false),
            TaskEntity(title = "喝水", description = "第1杯", isDone = true),
            TaskEntity(title = "交報告", description = "14:00 截止", isDone = false)
        )
        TaskListScreen(
            tasks = fakeTasks,
            onAddTask = {},
            onEditTask = {},
            onToggleTask = {},
            onDeleteTask = {}
        )
    }
}
