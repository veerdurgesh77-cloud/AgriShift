package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AgriShiftEntity::class], version = 1, exportSchema = false)
abstract class AgriShiftDatabase : RoomDatabase() {
    abstract fun agriShiftDao(): AgriShiftDao

    companion object {
        @Volatile
        private var INSTANCE: AgriShiftDatabase? = null

        fun getDatabase(context: Context): AgriShiftDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AgriShiftDatabase::class.java,
                    "agrishift_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
