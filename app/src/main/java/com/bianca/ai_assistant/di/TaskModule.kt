package com.bianca.ai_assistant.di

import com.bianca.ai_assistant.infrastructure.room.task.TaskDao
import com.bianca.ai_assistant.viewModel.task.ITaskRepository
import com.bianca.ai_assistant.viewModel.task.TaskRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
object TaskModule {

    @Provides
    @ViewModelScoped
    fun provideTaskRepository(taskDao: TaskDao): ITaskRepository =
        TaskRepositoryImpl(taskDao)
}