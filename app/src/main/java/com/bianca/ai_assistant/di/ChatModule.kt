package com.bianca.ai_assistant.di

import com.bianca.ai_assistant.infrastructure.ai.AiClassifier
import com.bianca.ai_assistant.infrastructure.ai.FirebaseGeminiClassifier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

//@Module
//@InstallIn(ViewModelComponent::class)
//object ChatModule {
//    @Provides
//    @ViewModelScoped
//    fun provideAiClassifier(): AiClassifier {
//        return FirebaseGeminiClassifier()
//    }
//}