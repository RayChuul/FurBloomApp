package com.example.furbloomappmsd

import android.app.Application
import com.example.furbloomappmsd.data.FurBloomDatabase
import com.example.furbloomappmsd.data.PetRepository
import com.example.furbloomappmsd.data.ReminderRepository

// FIXED: Reverted to a simpler version. The coroutine scope was not correctly implemented
// and was causing confusion. This simpler setup is more stable with the database fix.
class PetApplication : Application() {
    val database by lazy { FurBloomDatabase.getDatabase(this) }
    val petRepository by lazy { PetRepository(database.petDao()) }
    val reminderRepository by lazy { ReminderRepository(database.petReminderDao()) }
}
