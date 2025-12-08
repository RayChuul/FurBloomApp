package com.example.furbloomappmsd.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey@Entity(
    tableName = "pet_reminders",
    foreignKeys = [ForeignKey(
        entity = Pet::class,
        parentColumns = ["id"],
        childColumns = ["petId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class PetReminder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val petId: Int,
    val petName: String,
    val reminderType: String,
    val description: String,
    val dateTime: Long,
    var isCompleted: Boolean = false,
    val notes: String = "",
    val repeat: String = "None" // FIXED: Add 'repeat' field with a default value
)
