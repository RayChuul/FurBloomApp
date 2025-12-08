package com.example.furbloomappmsd.data

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPet(pet: Pet)

    @Query("SELECT * FROM pets ORDER BY id ASC")
    fun getAllPets(): Flow<List<Pet>>

    @Query("SELECT * FROM pets WHERE id = :id")
    fun getPetById(id: Int): LiveData<Pet>

    @Update
    suspend fun updatePet(pet: Pet)

    // ADD THIS FUNCTION
    @Delete
    suspend fun deletePet(pet: Pet)
}
