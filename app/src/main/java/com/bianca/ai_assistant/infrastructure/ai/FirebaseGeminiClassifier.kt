package com.bianca.ai_assistant.infrastructure.ai

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend

class FirebaseGeminiClassifier: AiClassifier {

    // 將 model 的初始化改為 lazy 或在需要時才取得
    private val generativeModel by lazy {
        Firebase.ai(backend = GenerativeBackend.googleAI())
            .generativeModel("gemini-2.0-flash")
    }

    override suspend fun classify(text: String): String {
        // Provide a prompt that contains text
        // 呼叫 generateContent 時，generativeModel 會被首次初始化
        val response = generativeModel.generateContent(text)
        Log.v("generativeModel", "${response.text}")

        return try {
            // 呼叫 Gemini 進行分類
            val response = generativeModel.generateContent(text)
            response.text?.trim()?.lowercase()?.let {
                when {
                    it.contains("expense") -> "expense"
                    it.contains("note")    -> "note"
                    else                   -> "note"
                }
            }?: "note" // 若回傳內容為空，預設為 note
        } catch (e: FirebaseException) {
            // Firebase 層面錯誤 (如初始化、網路等)
            fallbackType(text)
        } catch (e: Exception) {
            // 其他未知錯誤通通 fallback
            Log.e("FirebaseGeminiClassifier", "分類失敗: ${e.message}", e)
            fallbackType(text)
        }
    }

    override suspend fun getReplay(text: String): String {
        val response = generativeModel.generateContent(text)
        Log.v("generativeModel", "${response.text}")

        return try {
            // 呼叫 Gemini 進行分類
            val response = generativeModel.generateContent(text)
            response.text?.trim()?.lowercase() ?: "" // 若回傳內容為空，預設為 note
        } catch (e: FirebaseException) {
            // Firebase 層面錯誤 (如初始化、網路等)
//            fallbackType(text)
            e.message?:""
        } catch (e: Exception) {
            // 其他未知錯誤通通 fallback
            Log.e("FirebaseGeminiClassifier", "分類失敗: ${e.message}", e)
            e.message?:""
        }
    }

    /** Fallback 規則：若文字中有數字+「元」就當 expense，否則 note */
    private fun fallbackType(text: String): String =
        if ("""\d+元""".toRegex().containsMatchIn(text)) "expense" else "note"
}
