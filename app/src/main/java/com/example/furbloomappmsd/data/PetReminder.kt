package com.example.furbloomappmsd.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pet_reminders")
data class PetReminder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val petName: String,
    val reminderType: String, // e.g., "Feeding", "Medication", "Vet Visit", "Grooming"
    val description: String,
    val dateTime: Long, // Store as timestamp
    val isCompleted: Boolean = false,
    val priority: String = "Medium", // "Low", "Medium", "High"
    val notes: String = ""
)