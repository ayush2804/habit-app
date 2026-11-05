package com.ayushpandey.habitapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ayushpandey.habitapp.HabitApplication
import com.ayushpandey.habitapp.data.HabitColors
import com.ayushpandey.habitapp.data.HabitEntity
import com.ayushpandey.habitapp.data.HabitOutcome
import com.ayushpandey.habitapp.data.HabitRepository
import com.ayushpandey.habitapp.data.HabitStats
import com.ayushpandey.habitapp.data.HabitTemplate
import com.ayushpandey.habitapp.data.HabitWithToday
import com.ayushpandey.habitapp.data.ScheduleDays
import com.ayushpandey.habitapp.data.StreakCalculator
import com.ayushpandey.habitapp.data.TemplateCategory
import com.ayushpandey.habitapp.reminders.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class AddStep { TEMPLATES, CUSTOMIZE }

data class HomeUiState(
    val showAddDialog: Boolean = false,
    val addStep: AddStep = AddStep.TEMPLATES,
    val templateCategory: TemplateCategory? = null,
    val addTitle: String = "",
    val addColor: Int = HabitColors.Lagoon.argb,
    val addSchedule: Int = ScheduleDays.ALL,
    val addReminderEnabled: Boolean = false,
    val addReminderHour: Int = 9,
    val addReminderMinute: Int = 0,
    val addError: String? = null,
    val showOutcomePicker: HabitEntity? = null,
    val reflectionTarget: HabitEntity? = null,
    val pendingOutcome: HabitOutcome? = null,
    val reflectionNote: String = "",
    val reflectionError: String? = null
)

class HabitViewModel(
    application: Application,
    private val repository: HabitRepository
) : AndroidViewModel(application) {

    val habits: StateFlow<List<HabitWithToday>> = repository
        .observeActiveHabitsWithToday()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _ui = MutableStateFlow(HomeUiState())
    val ui: StateFlow<HomeUiState> = _ui.asStateFlow()

    private val _stats = MutableStateFlow<HabitStats?>(null)
    val stats: StateFlow<HabitStats?> = _stats.asStateFlow()

    init {
        refreshStats()
    }

    fun refreshStats() {
        viewModelScope.launch {
            _stats.value = repository.stats()
        }
    }

    fun openAddDialog() {
        _ui.update {
            HomeUiState(
                showAddDialog = true,
                addStep = AddStep.TEMPLATES,
                addColor = HabitColors.options.random()
            )
        }
    }

    fun dismissAddDialog() {
        _ui.update { HomeUiState() }
    }

    fun onTemplateCategory(category: TemplateCategory?) =
        _ui.update { it.copy(templateCategory = category) }

    fun goCustomize() = _ui.update { it.copy(addStep = AddStep.CUSTOMIZE) }

    fun applyTemplate(template: HabitTemplate) {
        _ui.update {
            it.copy(
                addStep = AddStep.CUSTOMIZE,
                addTitle = template.title,
                addColor = template.colorArgb,
                addSchedule = template.scheduleDays,
                addReminderEnabled = template.reminderHour != null,
                addReminderHour = template.reminderHour ?: 9,
                addReminderMinute = template.reminderMinute,
                addError = null
            )
        }
    }

    fun onAddTitleChange(value: String) = _ui.update { it.copy(addTitle = value, addError = null) }
    fun onAddColorChange(value: Int) = _ui.update { it.copy(addColor = value) }
    fun onAddScheduleToggle(dayIso: Int) = _ui.update {
        it.copy(addSchedule = ScheduleDays.toggle(it.addSchedule, dayIso))
    }
    fun onAddReminderEnabled(value: Boolean) = _ui.update { it.copy(addReminderEnabled = value) }
    fun onAddReminderTime(hour: Int, minute: Int) =
        _ui.update { it.copy(addReminderHour = hour, addReminderMinute = minute) }

    fun saveNewHabit() {
        val state = _ui.value
        if (state.addTitle.isBlank()) {
            _ui.update { it.copy(addError = "Enter a habit name") }
            return
        }
        if (state.addSchedule == 0) {
            _ui.update { it.copy(addError = "Pick at least one day") }
            return
        }
        viewModelScope.launch {
            val id = repository.addHabit(
                title = state.addTitle,
                colorArgb = state.addColor,
                scheduleDays = state.addSchedule,
                reminderEnabled = state.addReminderEnabled,
                reminderHour = state.addReminderHour,
                reminderMinute = state.addReminderMinute
            )
            repository.getActiveHabitsOnce().firstOrNull { it.id == id }?.let {
                ReminderScheduler.schedule(getApplication(), it)
            }
            dismissAddDialog()
            refreshStats()
        }
    }

    fun onHabitCheckIn(habit: HabitEntity) {
        _ui.update { it.copy(showOutcomePicker = habit) }
    }

    fun dismissOutcomePicker() = _ui.update { it.copy(showOutcomePicker = null) }

    fun chooseOutcome(outcome: HabitOutcome) {
        val habit = _ui.value.showOutcomePicker ?: return
        _ui.update {
            it.copy(
                showOutcomePicker = null,
                reflectionTarget = habit,
                pendingOutcome = outcome,
                reflectionNote = "",
                reflectionError = null
            )
        }
    }

    fun onReflectionNoteChange(value: String) =
        _ui.update { it.copy(reflectionNote = value, reflectionError = null) }

    fun dismissReflection() {
        _ui.update {
            it.copy(
                reflectionTarget = null,
                pendingOutcome = null,
                reflectionNote = "",
                reflectionError = null
            )
        }
    }

    fun saveReflection() {
        val state = _ui.value
        val habit = state.reflectionTarget ?: return
        val outcome = state.pendingOutcome ?: return
        if (state.reflectionNote.isBlank()) {
            _ui.update {
                it.copy(
                    reflectionError = when (outcome) {
                        HabitOutcome.COMPLETED -> "Share how you feel"
                        HabitOutcome.FAILED -> "Share a reason or improvement"
                    }
                )
            }
            return
        }
        viewModelScope.launch {
            repository.saveCheckIn(habit, outcome, state.reflectionNote)
            dismissReflection()
            refreshStats()
        }
    }

    fun undoToday(habit: HabitEntity) {
        viewModelScope.launch {
            repository.clearTodayCheckIn(habit)
            refreshStats()
        }
    }

    fun isDueToday(habit: HabitEntity): Boolean =
        StreakCalculator.isDue(habit.scheduleDays, LocalDate.now())

    companion object {
        fun factory(app: HabitApplication): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HabitViewModel(app, app.repository) as T
                }
            }
    }
}
