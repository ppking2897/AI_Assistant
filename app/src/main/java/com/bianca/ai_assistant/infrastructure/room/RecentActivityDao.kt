package com.bianca.ai_assistant.infrastructure.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentActivityDao {
    @Insert
    suspend fun insert(activity: RecentActivityEntity)
    @Query("SELECT * FROM recent_activity ORDER BY timestamp DESC")
    fun getAllRecentActivities(): Flow<List<RecentActivityEntity>>
}