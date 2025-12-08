package com.example.furbloomappmsd.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Pet::class, PetReminder::class], version = 2, exportSchema = false)
abstract class FurBloomDatabase : RoomDatabase() {

    abstract fun petDao(): PetDao
    abstract fun petReminderDao(): PetReminderDao

    companion object {
        @Volatile
        private var INSTANCE: FurBloomDatabase? = null

        fun getDatabase(context: Context): FurBloomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FurBloomDatabase::class.java,
                    "furbloom_database"
                )
                    .fallbackToDestructiveMigration()
                    // FIXED: This line, which caused the ANR, has been removed.
                    // .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
