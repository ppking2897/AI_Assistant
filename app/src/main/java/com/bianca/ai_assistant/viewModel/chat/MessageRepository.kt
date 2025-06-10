package com.bianca.ai_assistant.viewModel.chat

import com.bianca.ai_assistant.infrastructure.room.MessageDao
import com.bianca.ai_assistant.infrastructure.room.MessageEntity
import jakarta.inject.Inject

interface IMessageRepository {
    suspend fun getAll(): List<MessageEntity>
    suspend fun insert(entity: MessageEntity)
    suspend fun clearAll()
}

class MessageRepository @Inject constructor(
    private val messageDao: MessageDao
): IMessageRepository {
    override suspend fun getAll() = messageDao.getAllMessages()
    override suspend fun insert(entity: MessageEntity) = messageDao.insertMessage(entity)
    override suspend fun clearAll() = messageDao.clearAll()
}