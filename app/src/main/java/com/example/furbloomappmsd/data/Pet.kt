package com.example.furbloomappmsd.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pets")
data class Pet(
    @PrimaryKey(autoGenerate = true)val id: Int = 0,

    val name: String,
    val birthDate: Long? = null,
    val species: String? = null,
    val gender: String? = null,
    val medicalHistory: String? = null,
    val notes: String? = null,
    val photoUri: String? = null
)
