package com.example.furbloomappmsd.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Pet::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun petDao(): PetDao
}
