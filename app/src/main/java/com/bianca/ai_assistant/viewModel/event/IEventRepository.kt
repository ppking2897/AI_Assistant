package com.bianca.ai_assistant.viewModel.event

import com.bianca.ai_assistant.infrastructure.room.event.EventDao
import com.bianca.ai_assistant.infrastructure.room.event.EventEntity
import kotlinx.coroutines.flow.Flow

interface IEventRepository {
    fun getAllEventsFlow(): Flow<List<EventEntity>>
    suspend fun insert(event: EventEntity): Long
    suspend fun update(event: EventEntity)
    suspend fun delete(event: EventEntity)
}

class EventRepositoryImpl(private val dao: EventDao) : IEventRepository {
    override fun getAllEventsFlow(): Flow<List<EventEntity>> = dao.getAllEventsFlow()
    override suspend fun insert(event: EventEntity): Long = dao.insert(event)
    override suspend fun update(event: EventEntity) = dao.update(event)
    override suspend fun delete(event: EventEntity) = dao.delete(event)
}
