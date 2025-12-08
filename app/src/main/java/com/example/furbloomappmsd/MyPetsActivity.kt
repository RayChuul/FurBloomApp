package com.example.furbloomappmsd

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furbloomappmsd.data.Pet
import com.example.furbloomappmsd.ui.PetAdapter
import com.example.furbloomappmsd.ui.PetViewModel
import com.example.furbloomappmsd.ui.PetViewModelFactory
import com.example.furbloomappmsd.ui.AddPetActivity

class MyPetsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PetAdapter

    // Initialize ViewModel with factory
    private val viewModel: PetViewModel by viewModels {
        PetViewModelFactory((application as PetApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_pets)

        recyclerView = findViewById(R.id.recyclerView_pets)

        // Initialize adapter with empty list; will update from LiveData
        adapter = PetAdapter(
            pets = mutableListOf(), // Use mutable list
            onPetClick = { pet ->
                // Open Add/Edit Reminder Activity for selected pet
                val intent = Intent(this, AddEditReminderActivity::class.java)
                intent.putExtra("PET_NAME", pet.name)
                startActivity(intent)
            },
            onAddPetClick = {
                // Open Add Pet Activity
                val intent = Intent(this, AddPetActivity::class.java)
                startActivity(intent)
            }
        )

        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter

        // Observe pets LiveData from ViewModel
        viewModel.allPets.observe(this) { pets ->
            adapter.updatePets(pets)
        }
    }
}
