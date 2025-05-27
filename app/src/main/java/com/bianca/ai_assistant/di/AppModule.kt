package com.bianca.ai_assistant.di

import android.content.Context
import androidx.room.Room
import com.bianca.ai_assistant.infrastructure.room.AppDatabase
import com.bianca.ai_assistant.infrastructure.room.task.TaskDao
import com.bianca.ai_assistant.viewModel.task.TaskRepository
import com.bianca.ai_assistant.viewModel.task.TaskRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


}
