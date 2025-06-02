package com.bianca.ai_assistant.viewModel.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bianca.ai_assistant.infrastructure.room.task.TaskEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


/**
 * addTask：新增任務，執行後自動刷新清單
 *
 * updateTask：更新指定任務（可用於任務編輯/細節調整）
 *
 * deleteTask：刪除指定任務
 *
 * toggleTask：切換完成/未完成狀態
 *
 * selectTask：設定目前選取的任務（可用於彈出編輯視窗等場景）
 */

enum class TaskFilter {
    ALL, UNFINISHED, FINISHED
}

@HiltViewModel
class TaskViewModel @Inject constructor(private val repository: ITaskRepository) : ViewModel() {

    private val _tasks = MutableStateFlow<List<TaskEntity>>(emptyList())
    val tasks: StateFlow<List<TaskEntity>> = _tasks.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _filter = MutableStateFlow(TaskFilter.ALL)
    val filter: StateFlow<TaskFilter> = _filter


    val filteredTasks: StateFlow<List<TaskEntity>> =
        combine(tasks, searchQuery, filter) { list, query, filterType ->
            list
                .filter {
                    // 狀態篩選
                    when (filterType) {
                        TaskFilter.ALL -> true
                        TaskFilter.UNFINISHED -> !it.isDone
                        TaskFilter.FINISHED -> it.isDone
                    }
                }
                .filter {
                    // 關鍵字搜尋
                    query.isBlank() || it.title.contains(query, true) || (it.description?.contains(
                        query,
                        true
                    ) == true)
                }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch { _tasks.value = repository.getAllTasks() }
    }

    fun addTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.addTask(task)
            loadTasks()
        }
    }

    fun addTask(task: TaskEntity, onInserted: (TaskEntity) -> Unit) {
        viewModelScope.launch {
            val id = repository.insertTask(task)
            val saved = task.copy(id = id)
            onInserted(saved)
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task)
            loadTasks()
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
            loadTasks()
        }
    }

    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch {
            val updatedTask = task.copy(isDone = !task.isDone)
            repository.updateTask(updatedTask)
            loadTasks()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
    fun setFilter(filter: TaskFilter) {
        _filter.value = filter
    }

    fun getTaskByIdState(id: Long): StateFlow<TaskEntity?> {
        return tasks
            .map { list -> list.find { it.id == id } }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                null
            )
    }

    fun getTaskById(id: Long): TaskEntity? {
        return tasks.value.find { it.id == id }
    }
}

