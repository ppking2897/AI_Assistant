package com.bianca.ai_assistant.infrastructure.ai

interface AiClassifier {
    /**
     * 傳入使用者文字，回傳 "expense" 或 "note"
     */
    suspend fun classify(text: String): String

    suspend fun getReplay(text: String): String
}