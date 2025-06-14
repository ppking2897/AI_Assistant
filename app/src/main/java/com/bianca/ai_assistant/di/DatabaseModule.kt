package com.bianca.ai_assistant.di

import android.content.Context
import androidx.room.Room
import com.bianca.ai_assistant.infrastructure.room.AppDatabase
import com.bianca.ai_assistant.infrastructure.room.AppDatabase.DatabaseMigrations.MIGRATION_4_5
import com.bianca.ai_assistant.infrastructure.room.AppDatabase.DatabaseMigrations.MIGRATION_5_6
import com.bianca.ai_assistant.infrastructure.room.MessageDao
import com.bianca.ai_assistant.infrastructure.room.RecentActivityDao
import com.bianca.ai_assistant.infrastructure.room.article.ArticleDao
import com.bianca.ai_assistant.infrastructure.room.event.EventDao
import com.bianca.ai_assistant.infrastructure.room.task.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "task_app_db")
            .addMigrations(AppDatabase.DatabaseMigrations.MIGRATION_1_2)
            .addMigrations(AppDatabase.DatabaseMigrations.MIGRATION_2_3)
            .addMigrations(AppDatabase.DatabaseMigrations.MIGRATION_3_4)
            .addMigrations(MIGRATION_4_5)
            .addMigrations(MIGRATION_5_6)
            .build()

    @Provides
    @Singleton
    fun provideTaskDao(db: AppDatabase): TaskDao = db.taskDao()

    @Provides
    @Singleton
    fun provideArticleDao(db: AppDatabase): ArticleDao = db.articleDao()

    @Provides
    @Singleton
    fun provideMessageDao(db: AppDatabase): MessageDao =
        db.messageCao()

    @Provides
    @Singleton
    fun provideRepository(
        db: AppDatabase,
    ): RecentActivityDao {
        return db.recentActivityDao()
    }

    @Provides
    @Singleton
    fun provideEventDao(db: AppDatabase): EventDao = db.eventDao()
}