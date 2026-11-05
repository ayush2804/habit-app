package com.ayushpandey.habitapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class HabitConverters {
    @TypeConverter
    fun fromOutcome(value: HabitOutcome?): String? = value?.name

    @TypeConverter
    fun toOutcome(value: String?): HabitOutcome? =
        value?.let { HabitOutcome.valueOf(it) }
}

@Database(
    entities = [HabitEntity::class, CheckInEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(HabitConverters::class)
abstract class HabitDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao

    companion object {
        @Volatile
        private var instance: HabitDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS habits_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        title TEXT NOT NULL,
                        createdAtEpochMs INTEGER NOT NULL,
                        colorArgb INTEGER NOT NULL,
                        scheduleDays INTEGER NOT NULL,
                        reminderEnabled INTEGER NOT NULL,
                        reminderHour INTEGER NOT NULL,
                        reminderMinute INTEGER NOT NULL,
                        archived INTEGER NOT NULL,
                        currentStreak INTEGER NOT NULL,
                        bestStreak INTEGER NOT NULL,
                        lastUpdatedEpochMs INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO habits_new (
                        id, title, createdAtEpochMs, colorArgb, scheduleDays,
                        reminderEnabled, reminderHour, reminderMinute, archived,
                        currentStreak, bestStreak, lastUpdatedEpochMs
                    )
                    SELECT id, title, createdAtEpochMs,
                           ${HabitColors.options.first()}, ${ScheduleDays.ALL},
                           0, 9, 0, 0, 0, 0, lastUpdatedEpochMs
                    FROM habits
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE habits")
                db.execSQL("ALTER TABLE habits_new RENAME TO habits")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS check_ins (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        habitId INTEGER NOT NULL,
                        epochDay INTEGER NOT NULL,
                        outcome TEXT NOT NULL,
                        note TEXT NOT NULL,
                        createdAtEpochMs INTEGER NOT NULL,
                        FOREIGN KEY(habitId) REFERENCES habits(id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_check_ins_habitId ON check_ins(habitId)")
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_check_ins_habitId_epochDay ON check_ins(habitId, epochDay)"
                )

                // Best-effort migrate old reflections into check_ins (one per reflection day).
                db.execSQL(
                    """
                    INSERT OR IGNORE INTO check_ins (habitId, epochDay, outcome, note, createdAtEpochMs)
                    SELECT habitId,
                           (createdAtEpochMs / 86400000),
                           outcome,
                           note,
                           createdAtEpochMs
                    FROM reflections
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE IF EXISTS reflections")
            }
        }

        fun get(context: Context): HabitDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    HabitDatabase::class.java,
                    "habit_app.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
        }
    }
}
