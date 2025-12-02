package com.example.furbloomappmsd.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PetReminder::class], version = 1, exportSchema = false)
abstract class PetReminderDatabase : RoomDatabase() {

    abstract fun petReminderDao(): PetReminderDao

    companion object {
        @Volatile
        private var INSTANCE: PetReminderDatabase? = null

        fun getDatabase(context: Context): PetReminderDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    PetReminderDatabase::class.java,
                    "pet_reminder_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}