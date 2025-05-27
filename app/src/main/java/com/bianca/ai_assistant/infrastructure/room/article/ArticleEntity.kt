package com.bianca.ai_assistant.infrastructure.room.article

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.bianca.ai_assistant.infrastructure.room.task.TaskEntity

@Entity(
    tableName = "articles",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.SET_NULL // 若關聯任務被刪除，taskId設為null
        )
    ],
    indices = [Index(value = ["taskId"])]
)
data class ArticleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val title: String,           // 記事標題
    val content: String,         // 記事內容

    val createdAt: Long = System.currentTimeMillis(), // 建立時間
    val updatedAt: Long = System.currentTimeMillis(), // 最後編輯時間（可選）

    val taskId: Long? = null    // 可選，關聯的任務id
)