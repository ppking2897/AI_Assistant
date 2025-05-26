package com.bianca.ai_assistant.infrastructure

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val description: String? = null,
    val dueTime: Long? = null, // 可用時間戳，null 表無提醒
    val isDone: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)