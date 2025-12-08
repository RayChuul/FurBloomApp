package com.example.furbloomappmsd

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furbloomappmsd.ui.AddPetActivity
import com.example.furbloomappmsd.ui.PetAdapter
import com.example.furbloomappmsd.ui.PetViewModel
import com.example.furbloomappmsd.ui.PetViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MyPetsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PetAdapter
    private lateinit var fabAddPet: FloatingActionButton // FIXED: Added FAB

    private val viewModel: PetViewModel by viewModels {
        PetViewModelFactory((application as PetApplication).petRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_pets)

        val toolbar: MaterialToolbar = findViewById(R.id.custom_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Pets"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.recyclerView_pets)
        fabAddPet = findViewById(R.id.fab_add_pet) // FIXED: Find the FAB

        setupRecyclerView()

        viewModel.allPets.observe(this) { pets ->
            adapter.submitList(pets)
        }

        // FIXED: Set click listener for the new FAB
        fabAddPet.setOnClickListener {
            startActivity(Intent(this, AddPetActivity::class.java))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupRecyclerView() {
        // FIXED: The adapter no longer needs the onAddPetClick lambda
        adapter = PetAdapter { pet ->
            val intent = Intent(this, PetDetailActivity::class.java).apply {
                putExtra("PET_ID", pet.id)
            }
            startActivity(intent)
        }

        recyclerView.adapter = adapter
        // FIXED: The layout manager no longer needs a complex spanSizeLookup
        recyclerView.layoutManager = GridLayoutManager(this, 2)
    }
}
