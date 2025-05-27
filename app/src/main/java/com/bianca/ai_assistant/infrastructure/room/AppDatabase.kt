package com.bianca.ai_assistant.infrastructure.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bianca.ai_assistant.infrastructure.room.article.ArticleDao
import com.bianca.ai_assistant.infrastructure.room.article.ArticleEntity
import com.bianca.ai_assistant.infrastructure.room.task.TaskDao
import com.bianca.ai_assistant.infrastructure.room.task.TaskEntity

@Database(entities = [TaskEntity::class, ArticleEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun articleDao(): ArticleDao

    object DatabaseMigrations {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 建立新表格
                db.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS articles (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                content TEXT NOT NULL,
                createdAt INTEGER NOT NULL,
                updatedAt INTEGER NOT NULL,
                taskId INTEGER,
                FOREIGN KEY(taskId) REFERENCES tasks(id) ON DELETE SET NULL
            )
        """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_articles_taskId ON articles(taskId)")
            }
        }
    }
}
