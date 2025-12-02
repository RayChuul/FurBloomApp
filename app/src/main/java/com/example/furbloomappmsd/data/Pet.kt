package com.example.furbloomappmsd.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pets")
data class Pet(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val species: String,
    val age: String,
    val gender: String,
    val notes: String = "",
    val photoUri: String = "" // Store photo path
)