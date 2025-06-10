package com.bianca.ai_assistant.infrastructure.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages ORDER BY time ASC")
    suspend fun getAllMessages(): List<MessageEntity>

    @Insert
    suspend fun insertMessage(message: MessageEntity)

    @Query("DELETE FROM messages")
    suspend fun clearAll()
}