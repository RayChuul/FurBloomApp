package com.example.furbloomappmsd.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.furbloomappmsd.data.PetReminder
import com.example.furbloomappmsd.data.ReminderRepository
import kotlinx.coroutines.launch

class ReminderViewModel(private val repository: ReminderRepository) : ViewModel() {

    val allReminders: LiveData<List<PetReminder>> = repository.allReminders

    fun getRemindersForPet(petId: Int): LiveData<List<PetReminder>> {
        return repository.getRemindersForPet(petId)
    }

    fun insert(reminder: PetReminder) = viewModelScope.launch {
        repository.insert(reminder)
    }

    fun update(reminder: PetReminder) = viewModelScope.launch {
        repository.update(reminder)
    }

    fun delete(reminder: PetReminder) = viewModelScope.launch {
        repository.delete(reminder)
    }
}