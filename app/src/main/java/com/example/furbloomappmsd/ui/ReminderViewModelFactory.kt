package com.example.furbloomappmsd.ui // FIXED: Changed package from 'viewmodel' to 'ui'

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.furbloomappmsd.data.ReminderRepository

class ReminderViewModelFactory(private val repository: ReminderRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReminderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReminderViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
