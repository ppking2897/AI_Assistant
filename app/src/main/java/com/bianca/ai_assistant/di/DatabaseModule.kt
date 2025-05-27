package com.bianca.ai_assistant.di

import android.content.Context
import androidx.room.Room
import com.bianca.ai_assistant.infrastructure.room.AppDatabase
import com.bianca.ai_assistant.infrastructure.room.article.ArticleDao
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
            .build()

    @Provides
    @Singleton
    fun provideTaskDao(db: AppDatabase): TaskDao = db.taskDao()

    @Provides
    @Singleton
    fun provideArticleDao(db: AppDatabase): ArticleDao = db.articleDao()
}