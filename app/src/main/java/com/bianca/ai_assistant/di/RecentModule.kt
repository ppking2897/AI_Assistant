package com.bianca.ai_assistant.di

import com.bianca.ai_assistant.infrastructure.room.RecentActivityDao
import com.bianca.ai_assistant.viewModel.IRecentActivityRepository
import com.bianca.ai_assistant.viewModel.RecentActivityRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object RecentModule {
    @Provides
    @ViewModelScoped
    fun provideRecentRepository(
         recentDao: RecentActivityDao,
    ): IRecentActivityRepository {
        return RecentActivityRepository(recentDao)
    }
}