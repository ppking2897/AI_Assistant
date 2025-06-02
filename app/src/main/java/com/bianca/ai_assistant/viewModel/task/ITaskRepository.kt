package com.bianca.ai_assistant.viewModel.task

import com.bianca.ai_assistant.infrastructure.room.task.TaskDao
import com.bianca.ai_assistant.infrastructure.room.task.TaskEntity
import kotlinx.coroutines.flow.Flow

interface ITaskRepository {
    suspend fun getAllTasks(): List<TaskEntity>
    suspend fun addTask(task: TaskEntity)
    suspend fun updateTask(task: TaskEntity)
    suspend fun deleteTask(task: TaskEntity)
    suspend fun getTaskById(id: Long): TaskEntity?
    fun getAllTasksFlow(): Flow<List<TaskEntity>>
    suspend fun insertTask(task: TaskEntity): Long
    // 可加搜尋、篩選等方法
}

class TaskRepositoryImpl(private val taskDao: TaskDao) : ITaskRepository {

    override suspend fun getAllTasks(): List<TaskEntity> = taskDao.getAllTasks()

    override suspend fun addTask(task: TaskEntity) {
        taskDao.insertTask(task)
    }

    override suspend fun updateTask(task: TaskEntity) {
        taskDao.updateTask(task)
    }

    override suspend fun deleteTask(task: TaskEntity) {
        taskDao.deleteTask(task)
    }

    override suspend fun getTaskById(id: Long): TaskEntity? = taskDao.getTaskById(id)

    override fun getAllTasksFlow(): Flow<List<TaskEntity>> = taskDao.getAllTasksFlow()
    override suspend fun insertTask(task: TaskEntity): Long = taskDao.insertTask(task)
}
