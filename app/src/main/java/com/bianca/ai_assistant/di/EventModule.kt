package com.bianca.ai_assistant.di

import com.bianca.ai_assistant.infrastructure.room.event.EventDao
import com.bianca.ai_assistant.viewModel.event.EventRepositoryImpl
import com.bianca.ai_assistant.viewModel.event.IEventRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object EventModule {
    @Provides
    @ViewModelScoped
    fun provideEventRepository(dao: EventDao): IEventRepository = EventRepositoryImpl(dao)
}
