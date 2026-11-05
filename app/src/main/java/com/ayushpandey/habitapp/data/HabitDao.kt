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
    @Query("SELECT * FROM habits ORDER BY createdAtEpochMs DESC")
    fun observeHabits(): Flow<List<HabitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Insert
    suspend fun insertReflection(reflection: ReflectionEntity): Long

    @Transaction
    suspend fun markHabitWithReflection(
        habit: HabitEntity,
        outcome: HabitOutcome,
        note: String
    ) {
        val now = System.currentTimeMillis()
        updateHabit(
            habit.copy(
                lastOutcome = outcome,
                lastUpdatedEpochMs = now
            )
        )
        insertReflection(
            ReflectionEntity(
                habitId = habit.id,
                outcome = outcome,
                note = note.trim(),
                createdAtEpochMs = now
            )
        )
    }

    @Query("SELECT * FROM reflections WHERE habitId = :habitId ORDER BY createdAtEpochMs DESC")
    fun observeReflections(habitId: Long): Flow<List<ReflectionEntity>>
}
