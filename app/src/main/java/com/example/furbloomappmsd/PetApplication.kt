package com.example.furbloomappmsd

import android.app.Application
import com.example.furbloomappmsd.data.FurBloomDatabase
import com.example.furbloomappmsd.data.PetRepository
import com.example.furbloomappmsd.data.ReminderRepository // Make sure this import is added

class PetApplication : Application() {
    // Point to the new unified database
    private val database by lazy { FurBloomDatabase.getDatabase(this) }

    // **FIXED**: Create specific repositories for both DAOs
    val petRepository by lazy { PetRepository(database.petDao()) }
    val reminderRepository by lazy { ReminderRepository(database.petReminderDao()) }
}
