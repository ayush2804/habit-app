package com.ayushpandey.habitapp.data

import kotlinx.coroutines.flow.Flow

class HabitRepository(private val dao: HabitDao) {
    fun observeHabits(): Flow<List<HabitEntity>> = dao.observeHabits()

    suspend fun addHabit(title: String) {
        val trimmed = title.trim()
        require(trimmed.isNotEmpty()) { "Habit title cannot be empty" }
        dao.insertHabit(HabitEntity(title = trimmed))
    }

    suspend fun saveReflection(habit: HabitEntity, outcome: HabitOutcome, note: String) {
        require(note.trim().isNotEmpty()) { "Reflection note cannot be empty" }
        dao.markHabitWithReflection(habit, outcome, note)
    }

    fun observeReflections(habitId: Long): Flow<List<ReflectionEntity>> =
        dao.observeReflections(habitId)
}
