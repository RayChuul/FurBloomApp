package com.example.furbloomappmsd.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PetReminderDao {

    // Get all reminders ordered by date/time
    @Query("SELECT * FROM pet_reminders ORDER BY dateTime ASC")
    fun getAllReminders(): LiveData<List<PetReminder>>

    // Get only pending reminders (not completed)
    @Query("SELECT * FROM pet_reminders WHERE isCompleted = 0 ORDER BY dateTime ASC")
    fun getPendingReminders(): LiveData<List<PetReminder>>

    // Insert a new reminder
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: PetReminder)

    // Update an existing reminder
    @Update
    suspend fun updateReminder(reminder: PetReminder)

    // Delete a reminder
    @Delete
    suspend fun deleteReminder(reminder: PetReminder)

    // Get a single reminder by ID
    @Query("SELECT * FROM pet_reminders WHERE id = :id LIMIT 1")
    suspend fun getReminderById(id: Int): PetReminder?

    // Mark a reminder as completed/uncompleted
    @Query("UPDATE pet_reminders SET isCompleted = :completed WHERE id = :id")
    suspend fun setCompletion(id: Int, completed: Boolean)
}
