package com.bianca.ai_assistant.infrastructure.room.task

import androidx.room.*

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY dueTime ASC")
    suspend fun getAllTasks(): List<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskEntity?

    // 進階功能範例
    @Query("SELECT * FROM tasks WHERE isDone = :done")
    suspend fun getTasksByStatus(done: Boolean): List<TaskEntity>
}
