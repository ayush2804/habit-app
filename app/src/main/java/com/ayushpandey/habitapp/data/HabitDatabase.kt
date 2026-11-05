package com.ayushpandey.habitapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

class HabitConverters {
    @TypeConverter
    fun fromOutcome(value: HabitOutcome?): String? = value?.name

    @TypeConverter
    fun toOutcome(value: String?): HabitOutcome? =
        value?.let { HabitOutcome.valueOf(it) }
}

@Database(
    entities = [HabitEntity::class, ReflectionEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(HabitConverters::class)
abstract class HabitDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao

    companion object {
        @Volatile
        private var instance: HabitDatabase? = null

        fun get(context: Context): HabitDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    HabitDatabase::class.java,
                    "habit_app.db"
                ).build().also { instance = it }
            }
        }
    }
}
