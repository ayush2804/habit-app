package com.ayushpandey.habitapp.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate

class HabitRepository(private val dao: HabitDao) {

    fun observeActiveHabitsWithToday(): Flow<List<HabitWithToday>> {
        val today = LocalDate.now().toEpochDay()
        return combine(
            dao.observeActiveHabits(),
            dao.observeCheckInsForDay(today)
        ) { habits, checkIns ->
            val byHabit = checkIns.associateBy { it.habitId }
            habits.map { HabitWithToday(it, byHabit[it.id]) }
        }
    }

    fun observeHabit(id: Long): Flow<HabitEntity?> = dao.observeHabit(id)

    fun observeCheckIns(habitId: Long): Flow<List<CheckInEntity>> =
        dao.observeCheckIns(habitId)

    fun observeArchived(): Flow<List<HabitEntity>> = dao.observeArchivedHabits()

    suspend fun addHabit(
        title: String,
        colorArgb: Int = HabitColors.options.random(),
        scheduleDays: Int = ScheduleDays.ALL,
        reminderEnabled: Boolean = false,
        reminderHour: Int = 9,
        reminderMinute: Int = 0
    ): Long {
        val trimmed = title.trim()
        require(trimmed.isNotEmpty())
        return dao.insertHabit(
            HabitEntity(
                title = trimmed,
                colorArgb = colorArgb,
                scheduleDays = scheduleDays,
                reminderEnabled = reminderEnabled,
                reminderHour = reminderHour,
                reminderMinute = reminderMinute
            )
        )
    }

    suspend fun updateHabit(habit: HabitEntity) {
        dao.updateHabit(habit.copy(lastUpdatedEpochMs = System.currentTimeMillis()))
    }

    suspend fun archiveHabit(habit: HabitEntity) {
        dao.updateHabit(
            habit.copy(
                archived = true,
                reminderEnabled = false,
                lastUpdatedEpochMs = System.currentTimeMillis()
            )
        )
    }

    suspend fun restoreHabit(habit: HabitEntity) {
        dao.updateHabit(
            habit.copy(archived = false, lastUpdatedEpochMs = System.currentTimeMillis())
        )
    }

    suspend fun deleteHabit(id: Long) = dao.deleteHabit(id)

    suspend fun saveCheckIn(
        habit: HabitEntity,
        outcome: HabitOutcome,
        note: String,
        day: LocalDate = LocalDate.now()
    ) {
        val trimmed = note.trim()
        require(trimmed.isNotEmpty())
        val existing = dao.getCheckInForDay(habit.id, day.toEpochDay())
        val checkIn = CheckInEntity(
            id = existing?.id ?: 0,
            habitId = habit.id,
            epochDay = day.toEpochDay(),
            outcome = outcome,
            note = trimmed
        )
        val all = dao.getCheckIns(habit.id)
            .filterNot { it.epochDay == day.toEpochDay() } + checkIn
        val current = StreakCalculator.currentStreak(habit.scheduleDays, all, day)
        val best = maxOf(
            habit.bestStreak,
            StreakCalculator.bestStreak(habit.scheduleDays, all)
        )
        dao.saveCheckInAndStreaks(habit, checkIn, current, best)
    }

    suspend fun clearTodayCheckIn(habit: HabitEntity, day: LocalDate = LocalDate.now()) {
        val existing = dao.getCheckInForDay(habit.id, day.toEpochDay()) ?: return
        dao.deleteCheckIn(existing.id)
        val all = dao.getCheckIns(habit.id)
        val current = StreakCalculator.currentStreak(habit.scheduleDays, all, day)
        val best = StreakCalculator.bestStreak(habit.scheduleDays, all)
        dao.updateHabit(
            habit.copy(
                currentStreak = current,
                bestStreak = maxOf(habit.bestStreak, best),
                lastUpdatedEpochMs = System.currentTimeMillis()
            )
        )
    }

    suspend fun getActiveHabitsOnce(): List<HabitEntity> = dao.getActiveHabitsList()

    suspend fun getHabitsWithReminders(): List<HabitEntity> = dao.getHabitsWithReminders()

    suspend fun stats(): HabitStats {
        val today = LocalDate.now()
        val active = dao.getActiveHabitsList()
        val dueToday = active.count { StreakCalculator.isDue(it.scheduleDays, today) }
        val todayChecks = dao.getCheckInsSince(today.toEpochDay())
            .filter { it.epochDay == today.toEpochDay() }
        val activeIds = active.map { it.id }.toSet()
        val completedToday = todayChecks.count {
            it.outcome == HabitOutcome.COMPLETED && it.habitId in activeIds
        }
        val weekChecks = dao.getCheckInsSince(today.minusDays(6).toEpochDay())
        var dueSlots = 0
        var completedSlots = 0
        for (habit in active) {
            for (offset in 0L..6L) {
                val day = today.minusDays(6 - offset)
                if (StreakCalculator.isDue(habit.scheduleDays, day)) {
                    dueSlots++
                    if (weekChecks.any {
                            it.habitId == habit.id &&
                                it.epochDay == day.toEpochDay() &&
                                it.outcome == HabitOutcome.COMPLETED
                        }
                    ) {
                        completedSlots++
                    }
                }
            }
        }
        val rate = if (dueSlots == 0) 0f else completedSlots.toFloat() / dueSlots
        return HabitStats(
            activeHabits = active.size,
            dueToday = dueToday,
            completedToday = completedToday,
            longestStreak = active.maxOfOrNull { it.bestStreak } ?: 0,
            completionRate7d = rate,
            totalCheckIns = dao.countCheckIns()
        )
    }
}
