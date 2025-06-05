package com.bianca.ai_assistant.viewModel.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bianca.ai_assistant.infrastructure.room.event.EventEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId

@HiltViewModel
class EventViewModel @Inject constructor(private val repository: IEventRepository) : ViewModel() {

    private val _selectedMonth = MutableStateFlow(YearMonth.now())
    val selectedMonth: StateFlow<YearMonth> = _selectedMonth

    val events = repository.getAllEventsFlow().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val eventsForMonth: StateFlow<List<EventEntity>> = combine(events, selectedMonth) { events, month ->
        events.filter {
            val ym = Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate().let { d ->
                YearMonth.of(d.year, d.month)
            }
            ym == month
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun previousMonth() {
        _selectedMonth.value = _selectedMonth.value.minusMonths(1)
    }

    fun nextMonth() {
        _selectedMonth.value = _selectedMonth.value.plusMonths(1)
    }

    fun insertEvent(event: EventEntity) {
        viewModelScope.launch { repository.insert(event) }
    }

    fun updateEvent(event: EventEntity) {
        viewModelScope.launch { repository.update(event) }
    }

    fun deleteEvent(event: EventEntity) {
        viewModelScope.launch { repository.delete(event) }
    }
}
