package com.example.furbloomappmsd.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow

class PetRepository(private val petDao: PetDao) {

    val allPets: Flow<List<Pet>> = petDao.getAllPets()

    suspend fun insertPet(pet: Pet) {
        petDao.insertPet(pet)
    }

    fun getPetById(id: Int): LiveData<Pet> {
        return petDao.getPetById(id)
    }

    suspend fun updatePet(pet: Pet) {
        petDao.updatePet(pet)
    }

    suspend fun deletePet(pet: Pet) {
        petDao.deletePet(pet)
    }
}
