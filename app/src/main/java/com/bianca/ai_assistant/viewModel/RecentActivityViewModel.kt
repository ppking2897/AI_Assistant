package com.bianca.ai_assistant.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bianca.ai_assistant.infrastructure.room.RecentActivityEntity
import com.bianca.ai_assistant.infrastructure.room.article.ArticleEntity
import com.bianca.ai_assistant.infrastructure.room.task.TaskEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class RecentActivityViewModel @Inject constructor(
    private val repository: IRecentActivityRepository,
) : ViewModel() {

    // 所有活動清單（Flow）
    val activities: StateFlow<List<RecentActivityEntity>> =
        repository.getAllRecentActivities()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // ---------- 分事件記錄方法 ----------

    /** 新增任務/完成任務/刪除任務...等任務相關紀錄 */
    fun recordTaskEvent(action: String, task: TaskEntity) {
        viewModelScope.launch {
            repository.recordTaskEvent(action, task)
        }
    }

    /** 新增、編輯、刪除記事...等記事相關紀錄 */
    fun recordArticleEvent(action: String, article: ArticleEntity) {
        viewModelScope.launch {
            repository.recordArticleEvent(action, article)
        }
    }

    /** 提問 AI，記錄問題與回應（如需） */
    fun recordAiEvent(question: String, answer: String?) {
        viewModelScope.launch {
            repository.recordAiEvent(question, answer)
        }
    }
}
