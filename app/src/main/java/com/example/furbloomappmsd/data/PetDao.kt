package com.example.furbloomappmsd.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPet(pet: Pet)

    @Query("SELECT * FROM pets ORDER BY id ASC")
    fun getAllPets(): Flow<List<Pet>>

    @Query("DELETE FROM pets WHERE id = :petId")
    suspend fun deletePet(petId: Int)
}
