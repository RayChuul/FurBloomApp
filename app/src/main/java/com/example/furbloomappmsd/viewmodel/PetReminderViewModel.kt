package com.example.furbloomappmsd.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.furbloomappmsd.data.PetReminder
import com.example.furbloomappmsd.data.PetReminderDatabase
import kotlinx.coroutines.launch

class PetReminderViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = PetReminderDatabase.getDatabase(application).petReminderDao()

    // LiveData of all reminders
    val allReminders: LiveData<List<PetReminder>> = dao.getAllReminders()

    // LiveData of pending reminders
    val pendingReminders: LiveData<List<PetReminder>> = dao.getPendingReminders()

    // Insert a new reminder
    fun insert(reminder: PetReminder) = viewModelScope.launch {
        dao.insertReminder(reminder)
    }

    // Update an existing reminder
    fun update(reminder: PetReminder) = viewModelScope.launch {
        dao.updateReminder(reminder)
    }

    // Delete a reminder
    fun delete(reminder: PetReminder) = viewModelScope.launch {
        dao.deleteReminder(reminder)
    }

    // Toggle completion status
    fun toggleCompletion(reminder: PetReminder) = viewModelScope.launch {
        dao.setCompletion(reminder.id, !reminder.isCompleted)
    }

    // Fetch a single reminder by ID (suspend function)
    suspend fun getReminderById(id: Int): PetReminder? {
        return dao.getReminderById(id)
    }
}
