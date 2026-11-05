package com.ayushpandey.habitapp.data

import java.time.DayOfWeek
import java.time.LocalDate

object StreakCalculator {

    /**
     * Current streak: consecutive scheduled days ending today (or yesterday if today
     * is not yet completed) that were marked COMPLETED.
     */
    fun currentStreak(
        scheduleDays: Int,
        checkIns: List<CheckInEntity>,
        today: LocalDate = LocalDate.now()
    ): Int {
        val completedDays = checkIns
            .filter { it.outcome == HabitOutcome.COMPLETED }
            .map { it.epochDay }
            .toSet()

        var cursor = today
        // If today is scheduled but not completed, streak may still count through yesterday.
        if (isDue(scheduleDays, cursor) && cursor.toEpochDay() !in completedDays) {
            cursor = cursor.minusDays(1)
        }

        var streak = 0
        var guard = 0
        while (guard < 370) {
            guard++
            if (!isDue(scheduleDays, cursor)) {
                cursor = cursor.minusDays(1)
                continue
            }
            if (cursor.toEpochDay() in completedDays) {
                streak++
                cursor = cursor.minusDays(1)
            } else {
                break
            }
        }
        return streak
    }

    fun bestStreak(
        scheduleDays: Int,
        checkIns: List<CheckInEntity>
    ): Int {
        val completed = checkIns
            .filter { it.outcome == HabitOutcome.COMPLETED }
            .map { LocalDate.ofEpochDay(it.epochDay) }
            .sorted()
        if (completed.isEmpty()) return 0

        var best = 0
        var run = 0
        var expected: LocalDate? = null

        for (day in completed) {
            if (!isDue(scheduleDays, day)) continue
            if (expected == null) {
                run = 1
            } else if (day == expected) {
                run++
            } else {
                run = 1
            }
            best = maxOf(best, run)
            expected = nextDueDay(scheduleDays, day.plusDays(1))
        }
        return best
    }

    fun isDue(scheduleDays: Int, date: LocalDate): Boolean {
        val iso = date.dayOfWeek.value // Mon=1 … Sun=7
        return ScheduleDays.isScheduled(scheduleDays, iso)
    }

    private fun nextDueDay(scheduleDays: Int, from: LocalDate): LocalDate {
        var d = from
        repeat(8) {
            if (isDue(scheduleDays, d)) return d
            d = d.plusDays(1)
        }
        return from
    }

    fun weekOutcomes(
        checkIns: List<CheckInEntity>,
        today: LocalDate = LocalDate.now()
    ): List<HabitOutcome?> {
        val monday = today.with(DayOfWeek.MONDAY)
        val byDay = checkIns.associateBy { it.epochDay }
        return (0..6).map { offset ->
            byDay[monday.plusDays(offset.toLong()).toEpochDay()]?.outcome
        }
    }
}
