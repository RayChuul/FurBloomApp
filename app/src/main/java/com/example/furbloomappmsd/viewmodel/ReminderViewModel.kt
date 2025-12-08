package com.example.furbloomappmsd.viewmodel

// FIXED: Removed the incorrect and unused Jetpack Compose imports
import androidx.lifecycle.*
import com.example.furbloomappmsd.data.PetReminder
import com.example.furbloomappmsd.data.ReminderRepository
import kotlinx.coroutines.launch

/**
 * Modern ViewModel to cache and manage all data related to PetReminders.
 * It uses a repository to interact with the database, following best practices.
 */
class ReminderViewModel(private val repository: ReminderRepository) : ViewModel() {

    // LiveData for all reminders, used in MainActivity and RecyclingListActivity
    val allReminders: LiveData<List<PetReminder>> = repository.allReminders

    /**
     * Gets reminders for one specific pet.
     * Used in PetDetailActivity and AddEditReminderActivity.
     */
    fun getRemindersForPet(petId: Int): LiveData<List<PetReminder>> {
        return repository.getRemindersForPet(petId)
    }

    /**
     * Launch a new coroutine to insert a reminder in a non-blocking way.
     */
    fun insert(reminder: PetReminder) = viewModelScope.launch {
        repository.insert(reminder)
    }

    /**
     * Launch a new coroutine to update a reminder in a non-blocking way.
     */
    fun update(reminder: PetReminder) = viewModelScope.launch {
        repository.update(reminder)
    }

    /**
     * Launch a new coroutine to delete a reminder in a non-blocking way.
     */
    fun delete(reminder: PetReminder) = viewModelScope.launch {
        repository.delete(reminder)
    }
}

/**
 * Factory for creating a ReminderViewModel with a constructor that takes a ReminderRepository.
 * This is the standard way to pass arguments to a ViewModel.
 */
class ReminderViewModelFactory(private val repository: ReminderRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReminderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReminderViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
