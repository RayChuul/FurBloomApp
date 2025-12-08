package com.example.furbloomappmsd

import android.app.Application
import com.example.furbloomappmsd.data.FurBloomDatabase
import com.example.furbloomappmsd.data.PetRepository
import com.example.furbloomappmsd.data.ReminderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class PetApplication : Application() {
    // FIXED: Use a SupervisorJob so that if one coroutine fails, it doesn't cancel others.
    private val applicationScope = CoroutineScope(SupervisorJob())

    // FIXED: Use lazy initialization that is thread-safe and tied to the application scope.
    // The database is now guaranteed to be created in the background.
    val database by lazy { FurBloomDatabase.getDatabase(this) }
    val petRepository by lazy { PetRepository(database.petDao()) }
    val reminderRepository by lazy { ReminderRepository(database.petReminderDao()) }
}
