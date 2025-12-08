package com.example.furbloomappmsd

import android.app.Application
import com.example.furbloomappmsd.data.PetDatabase
import com.example.furbloomappmsd.data.PetRepository

class PetApplication : Application() {
    val database by lazy { PetDatabase.getInstance(this) }
    val repository by lazy { PetRepository(database.petDao()) }
}
