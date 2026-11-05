package com.ayushpandey.habitapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ayushpandey.habitapp.HabitApplication
import com.ayushpandey.habitapp.data.CheckInEntity
import com.ayushpandey.habitapp.data.HabitEntity
import com.ayushpandey.habitapp.data.HabitRepository
import com.ayushpandey.habitapp.data.ScheduleDays
import com.ayushpandey.habitapp.data.StreakCalculator
import com.ayushpandey.habitapp.reminders.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailEditState(
    val title: String = "",
    val scheduleDays: Int = ScheduleDays.ALL,
    val colorArgb: Int = 0,
    val reminderEnabled: Boolean = false,
    val reminderHour: Int = 9,
    val reminderMinute: Int = 0,
    val error: String? = null
)

class HabitDetailViewModel(
    application: Application,
    private val repository: HabitRepository,
    private val habitId: Long
) : AndroidViewModel(application) {

    val habit: StateFlow<HabitEntity?> = repository.observeHabit(habitId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val checkIns: StateFlow<List<CheckInEntity>> = repository.observeCheckIns(habitId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _edit = MutableStateFlow<DetailEditState?>(null)
    val edit: StateFlow<DetailEditState?> = _edit.asStateFlow()

    fun weekOutcomes() = StreakCalculator.weekOutcomes(checkIns.value)

    fun startEdit(habit: HabitEntity) {
        _edit.value = DetailEditState(
            title = habit.title,
            scheduleDays = habit.scheduleDays,
            colorArgb = habit.colorArgb,
            reminderEnabled = habit.reminderEnabled,
            reminderHour = habit.reminderHour,
            reminderMinute = habit.reminderMinute
        )
    }

    fun dismissEdit() {
        _edit.value = null
    }

    fun updateEdit(block: (DetailEditState) -> DetailEditState) {
        _edit.update { current -> current?.let(block) }
    }

    fun saveEdit(habit: HabitEntity) {
        val state = _edit.value ?: return
        if (state.title.isBlank()) {
            _edit.update { it?.copy(error = "Name required") }
            return
        }
        if (state.scheduleDays == 0) {
            _edit.update { it?.copy(error = "Pick at least one day") }
            return
        }
        viewModelScope.launch {
            val updated = habit.copy(
                title = state.title.trim(),
                scheduleDays = state.scheduleDays,
                colorArgb = state.colorArgb,
                reminderEnabled = state.reminderEnabled,
                reminderHour = state.reminderHour,
                reminderMinute = state.reminderMinute
            )
            repository.updateHabit(updated)
            ReminderScheduler.schedule(getApplication(), updated)
            dismissEdit()
        }
    }

    fun archive(habit: HabitEntity, onDone: () -> Unit) {
        viewModelScope.launch {
            ReminderScheduler.cancel(getApplication(), habit.id)
            repository.archiveHabit(habit)
            onDone()
        }
    }

    fun delete(habit: HabitEntity, onDone: () -> Unit) {
        viewModelScope.launch {
            ReminderScheduler.cancel(getApplication(), habit.id)
            repository.deleteHabit(habit.id)
            onDone()
        }
    }

    companion object {
        fun factory(app: HabitApplication, habitId: Long): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HabitDetailViewModel(app, app.repository, habitId) as T
                }
            }
    }
}
