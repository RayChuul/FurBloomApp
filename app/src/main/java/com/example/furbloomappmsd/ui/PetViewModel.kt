package com.example.furbloomappmsd.ui

import androidx.lifecycle.*
import com.example.furbloomappmsd.data.Pet
import com.example.furbloomappmsd.data.PetRepository
import kotlinx.coroutines.launch

class PetViewModel(private val repository: PetRepository) : ViewModel() {

    val allPets: LiveData<List<Pet>> = repository.allPets.asLiveData()

    fun addPet(pet: Pet) {
        viewModelScope.launch {
            repository.insertPet(pet)
        }
    }

    fun getPetById(id: Int): LiveData<Pet> {
        return repository.getPetById(id)
    }

    fun updatePet(pet: Pet) {
        viewModelScope.launch {
            repository.updatePet(pet)
        }
    }

    fun deletePet(pet: Pet) {
        viewModelScope.launch {
            repository.deletePet(pet)
        }
    }
}
