package com.example.furbloomappmsd.data

import androidx.lifecycle.LiveData

/**
 * Repository that abstracts access to the reminder data source.
 * It provides a clean API for the UI to fetch and manage reminder data.
 */
class ReminderRepository(private val petReminderDao: PetReminderDao) {

    // Gets all reminders from all pets, used by MainActivity.
    val allReminders: LiveData<List<PetReminder>> = petReminderDao.getAllReminders()

    /**
     * Gets reminders for one specific pet.
     * Used by PetDetailActivity and AddEditReminderActivity.
     */
    fun getRemindersForPet(petId: Int): LiveData<List<PetReminder>> {
        return petReminderDao.getRemindersForPet(petId)
    }

    /**
     * Inserts a new reminder into the database.
     */
    suspend fun insert(reminder: PetReminder) {
        petReminderDao.insertReminder(reminder)
    }

    /**
     * Updates an existing reminder in the database.
     */
    suspend fun update(reminder: PetReminder) {
        petReminderDao.updateReminder(reminder)
    }

    /**
     * Deletes a reminder from the database.
     */
    suspend fun delete(reminder: PetReminder) {
        petReminderDao.deleteReminder(reminder)
    }
}
