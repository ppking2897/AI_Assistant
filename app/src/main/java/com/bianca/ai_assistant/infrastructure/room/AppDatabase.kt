package com.bianca.ai_assistant.infrastructure.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bianca.ai_assistant.infrastructure.room.article.ArticleDao
import com.bianca.ai_assistant.infrastructure.room.article.ArticleEntity
import com.bianca.ai_assistant.infrastructure.room.event.EventDao
import com.bianca.ai_assistant.infrastructure.room.event.EventEntity
import com.bianca.ai_assistant.infrastructure.room.task.TaskDao
import com.bianca.ai_assistant.infrastructure.room.task.TaskEntity

@Database(
    entities = [MessageEntity::class, TaskEntity::class, ArticleEntity::class, RecentActivityEntity::class, EventEntity::class],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun articleDao(): ArticleDao
    abstract fun recentActivityDao(): RecentActivityDao
    abstract fun eventDao(): EventDao
    abstract fun messageCao(): MessageDao

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

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS recent_activity (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                type TEXT NOT NULL,
                action TEXT NOT NULL,
                refId INTEGER,
                title TEXT NOT NULL,
                summary TEXT,
                timestamp INTEGER NOT NULL
            )
            """.trimIndent()
                )
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS events (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                description TEXT,
                timestamp INTEGER NOT NULL
            )
            """.trimIndent()
                )
            }
        }

        // 在你的 Database 類別外部（或 companion object）定義：
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS messages (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                who TEXT NOT NULL,
                content TEXT NOT NULL,
                time INTEGER NOT NULL
            )
            """.trimIndent()
                )
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE messages ADD COLUMN type TEXT NOT NULL DEFAULT 'note' ")
            }
        }
    }
}
