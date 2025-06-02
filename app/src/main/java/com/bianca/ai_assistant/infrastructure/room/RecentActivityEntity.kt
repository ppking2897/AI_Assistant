package com.bianca.ai_assistant.infrastructure.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_activity")
data class RecentActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,            // "TASK"、"ARTICLE"、"AI"… 模組類型
    val action: String,          // "ADD"、"EDIT"、"DELETE"、"COMPLETE"… 操作類型
    val refId: Long?,            // 參考的主資料 id (如 taskId、articleId)
    val title: String,           // 顯示用標題（如任務名稱、記事標題、AI 問句）
    val summary: String?,        // 額外描述（可選，像 AI 回答/記事內容片段）
    val timestamp: Long          // 時間
)