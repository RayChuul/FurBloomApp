package com.example.furbloomappmsd.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PetReminderDao {

    // Get all reminders ordered by date/time
    @Query("SELECT * FROM pet_reminders ORDER BY dateTime ASC")
    fun getAllReminders(): LiveData<List<PetReminder>>

    // Get only pending reminders (not completed), ordered by time
    @Query("SELECT * FROM pet_reminders WHERE isCompleted = 0 ORDER BY dateTime ASC")
    fun getPendingReminders(): LiveData<List<PetReminder>>

    // NEW: Get all reminders for a specific pet
    @Query("SELECT * FROM pet_reminders WHERE petId = :petId ORDER BY dateTime ASC")
    fun getRemindersForPet(petId: Int): LiveData<List<PetReminder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: PetReminder)

    @Update
    suspend fun updateReminder(reminder: PetReminder)

    @Delete
    suspend fun deleteReminder(reminder: PetReminder)

    @Query("SELECT * FROM pet_reminders WHERE id = :id LIMIT 1")
    suspend fun getReminderById(id: Int): PetReminder?
}
