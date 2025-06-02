package com.bianca.ai_assistant.di

import com.bianca.ai_assistant.infrastructure.room.article.ArticleDao
import com.bianca.ai_assistant.viewModel.article.IArticleRepository
import com.bianca.ai_assistant.viewModel.article.ArticleRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ArticleModule {
    @Provides
    @ViewModelScoped
    fun provideArticleRepository(
        articleDao: ArticleDao,
    ): IArticleRepository {
        return ArticleRepositoryImpl(articleDao)
    }
}