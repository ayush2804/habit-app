package com.ayushpandey.habitapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ayushpandey.habitapp.data.HabitEntity
import com.ayushpandey.habitapp.data.HabitOutcome
import com.ayushpandey.habitapp.data.HabitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HabitUiState(
    val showAddDialog: Boolean = false,
    val addTitle: String = "",
    val addError: String? = null,
    val reflectionTarget: HabitEntity? = null,
    val pendingOutcome: HabitOutcome? = null,
    val reflectionNote: String = "",
    val reflectionError: String? = null,
    val showOutcomePicker: HabitEntity? = null
)

class HabitViewModel(
    private val repository: HabitRepository
) : ViewModel() {

    val habits: StateFlow<List<HabitEntity>> = repository
        .observeHabits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _ui = MutableStateFlow(HabitUiState())
    val ui: StateFlow<HabitUiState> = _ui.asStateFlow()

    fun openAddDialog() {
        _ui.update { it.copy(showAddDialog = true, addTitle = "", addError = null) }
    }

    fun dismissAddDialog() {
        _ui.update { it.copy(showAddDialog = false, addTitle = "", addError = null) }
    }

    fun onAddTitleChange(value: String) {
        _ui.update { it.copy(addTitle = value, addError = null) }
    }

    fun saveNewHabit() {
        val title = _ui.value.addTitle
        if (title.isBlank()) {
            _ui.update { it.copy(addError = "Enter a habit name") }
            return
        }
        viewModelScope.launch {
            repository.addHabit(title)
            dismissAddDialog()
        }
    }

    fun onHabitTapped(habit: HabitEntity) {
        _ui.update { it.copy(showOutcomePicker = habit) }
    }

    fun dismissOutcomePicker() {
        _ui.update { it.copy(showOutcomePicker = null) }
    }

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

    fun onReflectionNoteChange(value: String) {
        _ui.update { it.copy(reflectionNote = value, reflectionError = null) }
    }

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
            repository.saveReflection(habit, outcome, state.reflectionNote)
            dismissReflection()
        }
    }

    companion object {
        fun factory(repository: HabitRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HabitViewModel(repository) as T
                }
            }
    }
}
