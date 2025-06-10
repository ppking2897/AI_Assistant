package com.bianca.ai_assistant.viewModel.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bianca.ai_assistant.infrastructure.ai.AiClassifier
import com.bianca.ai_assistant.infrastructure.ai.FirebaseGeminiClassifier
import com.bianca.ai_assistant.infrastructure.room.MessageEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repo: MessageRepository
) : ViewModel() {

    private val classifier: AiClassifier = FirebaseGeminiClassifier()
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        loadMessages()
    }

    private fun loadMessages() {
        viewModelScope.launch {
            val msgs = repo.getAll()
            _uiState.update { it.copy(messages = msgs) }
        }
    }

    fun handleEvent(event: ChatUiEvent) {
        when (event) {
            is ChatUiEvent.InputChanged ->
                _uiState.update { it.copy(inputText = event.newText) }

            ChatUiEvent.SendMessage -> {
                viewModelScope.launch {
                    val content = _uiState.value.inputText.trim()
                    if (content.isNotEmpty()) {
                        val now = System.currentTimeMillis()
                        val type = classifier.classify(content)

                        // 插入使用者訊息並立刻更新畫面
                        val userMessage = MessageEntity(
                            who = "user",
                            content = content,
                            time = now,
                            type = type
                        )
                        repo.insert(userMessage)

                        _uiState.update {
                            it.copy(messages = repo.getAll(), inputText = "")
                        }

                        // 呼叫 AI API 取得回覆（假設你有 aiAssistant.getReply 方法）
                        val aiReplyContent = classifier.getReplay(content)
                        val aiMessage = MessageEntity(
                            who = "ai",
                            content = aiReplyContent,
                            time = System.currentTimeMillis(),
                            type = type // 或其他適當的分類
                        )
                        repo.insert(aiMessage)

                        _uiState.update {
                            it.copy(messages = repo.getAll())
                        }
                    }
                }
            }

            ChatUiEvent.ClearMessages ->
                viewModelScope.launch {
                    repo.clearAll()
                    _uiState.update { it.copy(messages = emptyList()) }
                }

            ChatUiEvent.ShowHistory -> {
                // Navigation handled in Route
            }
        }
    }
}

data class ChatUiState(
    val messages: List<MessageEntity> = emptyList(),
    val inputText: String = "",
)

sealed class ChatUiEvent {
    data class InputChanged(val newText: String) : ChatUiEvent()
    object SendMessage : ChatUiEvent()
    object ClearMessages : ChatUiEvent()
    object ShowHistory : ChatUiEvent()
}
