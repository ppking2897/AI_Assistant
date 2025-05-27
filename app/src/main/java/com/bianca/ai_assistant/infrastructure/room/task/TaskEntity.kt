package com.bianca.ai_assistant.infrastructure.room.task

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val description: String? = null,
    val dueTime: Long? = null, // 到期提醒用
    val isDone: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)