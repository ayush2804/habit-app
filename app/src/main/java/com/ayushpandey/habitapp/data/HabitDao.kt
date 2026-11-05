package com.ayushpandey.habitapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits WHERE archived = 0 ORDER BY createdAtEpochMs DESC")
    fun observeActiveHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE archived = 1 ORDER BY lastUpdatedEpochMs DESC")
    fun observeArchivedHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE id = :id LIMIT 1")
    fun observeHabit(id: Long): Flow<HabitEntity?>

    @Query("SELECT * FROM habits WHERE id = :id LIMIT 1")
    suspend fun getHabit(id: Long): HabitEntity?

    @Query("SELECT * FROM habits WHERE reminderEnabled = 1 AND archived = 0")
    suspend fun getHabitsWithReminders(): List<HabitEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun deleteHabit(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCheckIn(checkIn: CheckInEntity): Long

    @Query("SELECT * FROM check_ins WHERE habitId = :habitId ORDER BY epochDay DESC")
    fun observeCheckIns(habitId: Long): Flow<List<CheckInEntity>>

    @Query("SELECT * FROM check_ins WHERE habitId = :habitId ORDER BY epochDay DESC")
    suspend fun getCheckIns(habitId: Long): List<CheckInEntity>

    @Query("SELECT * FROM check_ins WHERE habitId = :habitId AND epochDay = :epochDay LIMIT 1")
    suspend fun getCheckInForDay(habitId: Long, epochDay: Long): CheckInEntity?

    @Query("SELECT * FROM check_ins WHERE epochDay = :epochDay")
    fun observeCheckInsForDay(epochDay: Long): Flow<List<CheckInEntity>>

    @Query("SELECT * FROM check_ins WHERE epochDay >= :fromDay")
    suspend fun getCheckInsSince(fromDay: Long): List<CheckInEntity>

    @Query("SELECT COUNT(*) FROM check_ins")
    suspend fun countCheckIns(): Int

    @Query("SELECT * FROM habits WHERE archived = 0 ORDER BY createdAtEpochMs DESC")
    suspend fun getActiveHabitsList(): List<HabitEntity>

    @Query("DELETE FROM check_ins WHERE id = :id")
    suspend fun deleteCheckIn(id: Long)

    @Transaction
    suspend fun saveCheckInAndStreaks(
        habit: HabitEntity,
        checkIn: CheckInEntity,
        currentStreak: Int,
        bestStreak: Int
    ) {
        upsertCheckIn(checkIn)
        updateHabit(
            habit.copy(
                currentStreak = currentStreak,
                bestStreak = bestStreak,
                lastUpdatedEpochMs = System.currentTimeMillis()
            )
        )
    }
}
