package com.example.furbloomappmsd.data

import androidx.lifecycle.LiveData


class ReminderRepository(private val petReminderDao: PetReminderDao) {

    // Gets all reminders from all pets, used by MainActivity.
    val allReminders: LiveData<List<PetReminder>> = petReminderDao.getAllReminders()

    // Gets all reminders for a specific pet, used by ReminderActivity.
    fun getRemindersForPet(petId: Int): LiveData<List<PetReminder>> {
        return petReminderDao.getRemindersForPet(petId)
    }

    suspend fun insert(reminder: PetReminder) {
        petReminderDao.insertReminder(reminder)
    }


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
