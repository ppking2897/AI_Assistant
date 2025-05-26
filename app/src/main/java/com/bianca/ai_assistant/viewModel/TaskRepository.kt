package com.bianca.ai_assistant.viewModel

import com.bianca.ai_assistant.infrastructure.TaskEntity

interface TaskRepository {
    suspend fun getAllTasks(): List<TaskEntity>
    suspend fun addTask(task: TaskEntity)
    suspend fun updateTask(task: TaskEntity)
    suspend fun deleteTask(task: TaskEntity)
    suspend fun getTaskById(id: Long): TaskEntity?
    // 可加搜尋、篩選等方法
}