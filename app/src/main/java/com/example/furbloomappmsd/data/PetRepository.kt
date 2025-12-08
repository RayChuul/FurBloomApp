package com.example.furbloomappmsd.data

import kotlinx.coroutines.flow.Flow

class PetRepository(private val petDao: PetDao) {

    val allPets: Flow<List<Pet>> = petDao.getAllPets()

    suspend fun insertPet(pet: Pet) {
        petDao.insertPet(pet)
    }
}
