package com.bianca.ai_assistant.ui.task

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bianca.ai_assistant.infrastructure.alarm.ScheduleAlarmWithPermissionCheck
import com.bianca.ai_assistant.infrastructure.room.task.TaskEntity
import com.bianca.ai_assistant.ui.dialog.TaskEditDialog
import com.bianca.ai_assistant.ui.theme.AI_AssistantTheme
import com.bianca.ai_assistant.utils.ScrollBottomNavigation
import com.bianca.ai_assistant.utils.formatTimeShort
import com.bianca.ai_assistant.viewModel.RecentActivityViewModel
import com.bianca.ai_assistant.viewModel.task.TaskFilter
import com.bianca.ai_assistant.viewModel.task.TaskViewModel
import kotlinx.coroutines.launch

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
 */

@ExperimentalMaterial3Api
@Composable
fun TaskListScreenWithViewModel(
    viewModel: TaskViewModel,
    recentActivityViewModel: RecentActivityViewModel,
    onTaskClick: (TaskEntity) -> Unit,
) {


    data class AlarmRequest(
        val taskId: Long,
        val dueTime: Long,
        val title: String,
        val requestKey: Long = System.currentTimeMillis(), // 確保每次都是新物件
    )

    val tasks by viewModel.filteredTasks.collectAsState()
    val filter by viewModel.filter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<TaskEntity?>(null) }
    var alarmInfo by remember { mutableStateOf<AlarmRequest?>(null) }

    // 統一所有 Dialog、提醒狀態
    TaskListScreenStateless(
        tasks = tasks,
        searchQuery = searchQuery,
        onSearchQueryChange = { viewModel.setSearchQuery(it) },
        filter = filter,
        onFilterChange = { viewModel.setFilter(it) },
        onAddTask = {
            editingTask = null
            showDialog = true
        },
        onEditTask = {
            editingTask = it
            showDialog = true
        },
        onTaskClick = onTaskClick,
        onToggleTask = {
            viewModel.toggleTask(it)
            recentActivityViewModel.recordTaskEvent(
                if (!it.isDone) "完成" else "未完成", it
            )
        },
        onDeleteTask = {
            viewModel.deleteTask(it)
            recentActivityViewModel.recordTaskEvent("刪除", it)
        },
        showDialog = showDialog,
        editingTask = editingTask,
        onDismissDialog = { showDialog = false },
        onConfirmDialog = { task ->
            val isNew = editingTask == null
            if (isNew) {
                viewModel.addTask(task) {
                    recentActivityViewModel.recordTaskEvent("新增", task)
                }

            } else {
                viewModel.updateTask(task)
                recentActivityViewModel.recordTaskEvent("編輯", task)
            }
            if (task.dueTime != null && task.dueTime!! > System.currentTimeMillis()) {
                alarmInfo =
                    AlarmRequest(task.id, task.dueTime, task.title, System.currentTimeMillis())
            }
            showDialog = false
        }
    )

    // 排定提醒
    alarmInfo?.let { req ->
        ScheduleAlarmWithPermissionCheck(
            taskId = req.taskId,
            timeMillis = req.dueTime,
            title = req.title,
            desc = "",
            onAlarmScheduled = { alarmInfo = null }
        )
    }
}

@ExperimentalMaterial3Api
@Composable
fun TaskListScreenStateless(
    tasks: List<TaskEntity>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    filter: TaskFilter,
    onFilterChange: (TaskFilter) -> Unit,
    onAddTask: () -> Unit,
    onEditTask: (TaskEntity) -> Unit,
    onTaskClick: (TaskEntity) -> Unit,  // 新增這行
    onToggleTask: (TaskEntity) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit,
    showDialog: Boolean,
    editingTask: TaskEntity?,
    onDismissDialog: () -> Unit,
    onConfirmDialog: (TaskEntity) -> Unit,
) {


    val focusManager = LocalFocusManager.current

    val listState = rememberLazyListState()

    ScrollBottomNavigation(listState)

    val chipColors = FilterChipDefaults.filterChipColors().copy(
        containerColor = Color(0xFFF5F5F5),   // 極淡灰
        labelColor = Color(0xFF607D8B),       // 藏青灰
        selectedContainerColor = Color(0xFFD0E8E2), // 淡綠灰
        selectedLabelColor = Color(0xFF00695C),     // 墨綠字
    )

    Column(
        Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }) {
        OutlinedTextField(
            shape = RoundedCornerShape(15.dp),
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            label = { Text("搜尋任務") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
        )
        // 篩選切換
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
            FilterChip(
                colors = chipColors,
                selected = filter == TaskFilter.ALL,
                onClick = { onFilterChange(TaskFilter.ALL) },
                label = { Text("全部") }
            )
            Spacer(Modifier.width(8.dp))
            FilterChip(
                colors = chipColors,
                selected = filter == TaskFilter.UNFINISHED,
                onClick = { onFilterChange(TaskFilter.UNFINISHED) },
                label = { Text("未完成") }
            )
            Spacer(Modifier.width(8.dp))
            FilterChip(
                colors = chipColors,
                selected = filter == TaskFilter.FINISHED,
                onClick = { onFilterChange(TaskFilter.FINISHED) },
                label = { Text("已完成") }
            )
        }

        TaskListScreen(
            tasks = tasks,
            onTaskClick = onTaskClick,
            onAddTask = onAddTask,
            onEditTask = onEditTask,
            onToggleTask = onToggleTask,
            onDeleteTask = onDeleteTask,
            lazyListState = listState,
        )
    }

    // 只依外層狀態顯示 Dialog
    if (showDialog) {
        TaskEditDialog(
            initialTask = editingTask,
            onDismiss = onDismissDialog,
            onConfirm = onConfirmDialog
        )
    }
}


@ExperimentalMaterial3Api
@Composable
fun TaskListScreen(
    tasks: List<TaskEntity>,
    onAddTask: () -> Unit = {},
    onTaskClick: (TaskEntity) -> Unit = {},
    onEditTask: (TaskEntity) -> Unit = {},
    onToggleTask: (TaskEntity) -> Unit = {},
    onDeleteTask: (TaskEntity) -> Unit = {},
    lazyListState: LazyListState,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { onAddTask() }) {
                Icon(Icons.Default.Add, contentDescription = "新增")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier =
                Modifier.padding(innerPadding),
            state = lazyListState,
            contentPadding = PaddingValues(bottom =16.dp)
        ) {
            items(tasks) { task ->
                TaskItemRow(
                    task = task,
                    onClick = { onTaskClick(task) },
                    onEdit = { onEditTask(task) },
                    onToggle = { onToggleTask(task) },
                    onDelete = { onDeleteTask(task) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}

@Composable
fun TaskItemRow(
    task: TaskEntity,
    onClick: () -> Unit = {},       // 點整行（詳情/跳頁）
    onEdit: () -> Unit = {},        // 點 icon（編輯）
    onToggle: () -> Unit = {},
    onDelete: () -> Unit = {},
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }   // 點整行跳詳情
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

            task.dueTime?.let {
                Text(
                    text = "提醒時間: ${formatTimeShort(it)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (it < System.currentTimeMillis()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }
        }
        IconButton(onClick = { onEdit() }) {
            Icon(Icons.Default.Edit, contentDescription = "編輯")
        }
        IconButton(onClick = { onDelete() }) {
            Icon(Icons.Default.Delete, contentDescription = "刪除")
        }
    }
}

@ExperimentalMaterial3Api
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewTaskListScreenStatelessNightMode() {
    AI_AssistantTheme {
        // 假資料
        val fakeTasks = listOf(
            TaskEntity(id = 1, title = "買牛奶", description = "去超市", isDone = false),
            TaskEntity(
                id = 2,
                title = "完成專案",
                description = "Android Room Hilt",
                isDone = true
            ),
        )
        var searchQuery by remember { mutableStateOf("") }
        var filter by remember { mutableStateOf(TaskFilter.ALL) }
        var showDialog by remember { mutableStateOf(false) }
        var editingTask by remember { mutableStateOf<TaskEntity?>(null) }

        TaskListScreenStateless(
            tasks = fakeTasks,
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            filter = filter,
            onFilterChange = { filter = it },
            onAddTask = {},
            onEditTask = { editingTask = it; showDialog = true },
            onToggleTask = {},
            onDeleteTask = {},
            showDialog = showDialog,
            editingTask = editingTask,
            onDismissDialog = { showDialog = false },
            onConfirmDialog = { showDialog = false },
            onTaskClick = {}
        )
    }

}

@ExperimentalMaterial3Api
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewTaskListScreenStateless() {
    AI_AssistantTheme {
        // 假資料
        var fakeTasks = listOf(
            TaskEntity(id = 1, title = "買牛奶", description = "去超市", isDone = false),
            TaskEntity(
                id = 2,
                title = "完成專案",
                description = "Android Room Hilt",
                isDone = true
            ),
        )
        var searchQuery by remember { mutableStateOf("") }
        var filter by remember { mutableStateOf(TaskFilter.ALL) }
        var showDialog by remember { mutableStateOf(false) }
        var editingTask by remember { mutableStateOf<TaskEntity?>(null) }

        TaskListScreenStateless(
            tasks = fakeTasks,
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            filter = filter,
            onFilterChange = { taskFilter ->
                val newTasks = fakeTasks.filter {
                    when (taskFilter) {
                        TaskFilter.ALL -> true
                        TaskFilter.UNFINISHED -> !it.isDone
                        TaskFilter.FINISHED -> it.isDone
                    }
                }

                fakeTasks = newTasks
                filter = taskFilter
            },
            onAddTask = {},
            onEditTask = { editingTask = it; showDialog = true },
            onToggleTask = {},
            onDeleteTask = {},
            showDialog = showDialog,
            editingTask = editingTask,
            onDismissDialog = { showDialog = false },
            onConfirmDialog = { showDialog = false },
            onTaskClick = {}
        )
    }

}