package com.bianca.ai_assistant.infrastructure.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val who: String,    // user/ai
    val content: String,
    val time: Long,
    val type: String = "note"    // "expense" 或 "note"
)