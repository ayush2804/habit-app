package com.ayushpandey.habitapp.data

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class HabitOutcome {
    COMPLETED,
    FAILED
}

/** Bit 0 = Monday … bit 6 = Sunday. Default = every day. */
object ScheduleDays {
    const val ALL = 0b1111111
    val LABELS = listOf("M", "T", "W", "T", "F", "S", "S")
    val FULL_LABELS = listOf(
        "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    )

    fun isScheduled(mask: Int, dayOfWeekIso: Int): Boolean {
        // dayOfWeekIso: Monday=1 … Sunday=7
        val bit = dayOfWeekIso - 1
        return (mask shr bit) and 1 == 1
    }

    fun toggle(mask: Int, dayOfWeekIso: Int): Int {
        val bit = dayOfWeekIso - 1
        return mask xor (1 shl bit)
    }
}

object HabitColors {
    val options = listOf(
        0xFF1B4D3E.toInt(),
        0xFF2F6F7E.toInt(),
        0xFFC45C4A.toInt(),
        0xFF8B6914.toInt(),
        0xFF4A5C8B.toInt(),
        0xFF6B4F7A.toInt()
    )

    fun asCompose(colorArgb: Int): Color = Color(colorArgb)
}

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val createdAtEpochMs: Long = System.currentTimeMillis(),
    val colorArgb: Int = HabitColors.options.first(),
    /** Which weekdays this habit is due (Monday=bit0). */
    val scheduleDays: Int = ScheduleDays.ALL,
    val reminderEnabled: Boolean = false,
    val reminderHour: Int = 9,
    val reminderMinute: Int = 0,
    val archived: Boolean = false,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val lastUpdatedEpochMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "check_ins",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("habitId"),
        Index(value = ["habitId", "epochDay"], unique = true)
    ]
)
data class CheckInEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    /** LocalDate.toEpochDay() for the calendar day of the check-in. */
    val epochDay: Long,
    val outcome: HabitOutcome,
    val note: String,
    val createdAtEpochMs: Long = System.currentTimeMillis()
)

data class HabitWithToday(
    val habit: HabitEntity,
    val todayCheckIn: CheckInEntity?
)

data class HabitStats(
    val activeHabits: Int,
    val dueToday: Int,
    val completedToday: Int,
    val longestStreak: Int,
    val completionRate7d: Float,
    val totalCheckIns: Int
)
