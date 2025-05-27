package com.bianca.ai_assistant.utils

import android.content.Context
import androidx.room.Room
import com.bianca.ai_assistant.infrastructure.room.AppDatabase

object DatabaseProvider {
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "task_app_db"
            ).build().also { INSTANCE = it }
        }
    }
}
