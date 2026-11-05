package com.ayushpandey.habitapp.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class HabitOutcome {
    COMPLETED,
    FAILED
}

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val createdAtEpochMs: Long = System.currentTimeMillis(),
    /** null = not yet marked today; COMPLETED / FAILED after a reflection is saved */
    val lastOutcome: HabitOutcome? = null,
    val lastUpdatedEpochMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "reflections",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("habitId")]
)
data class ReflectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    val outcome: HabitOutcome,
    val note: String,
    val createdAtEpochMs: Long = System.currentTimeMillis()
)
