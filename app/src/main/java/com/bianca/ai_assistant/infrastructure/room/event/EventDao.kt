package com.bianca.ai_assistant.infrastructure.room.event

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY timestamp ASC")
    fun getAllEventsFlow(): Flow<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity): Long

    @Update
    suspend fun update(event: EventEntity)

    @Delete
    suspend fun delete(event: EventEntity)

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventById(id: Long): EventEntity?
}
