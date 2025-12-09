package com.example.furbloomappmsd

import android.app.Application
import com.example.furbloomappmsd.data.FurBloomDatabase
import com.example.furbloomappmsd.data.PetRepository
import com.example.furbloomappmsd.data.ReminderRepository


class PetApplication : Application() {
    val database by lazy { FurBloomDatabase.getDatabase(this) }
    val petRepository by lazy { PetRepository(database.petDao()) }
    val reminderRepository by lazy { ReminderRepository(database.petReminderDao()) }
}
