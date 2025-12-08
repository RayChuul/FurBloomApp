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


class MyPetsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PetAdapter

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

        setupRecyclerView()

        viewModel.allPets.observe(this) { pets ->
            adapter.submitList(pets)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupRecyclerView() {
        adapter = PetAdapter(
            onPetClick = { pet ->
                val intent = Intent(this, PetDetailActivity::class.java).apply {
                    putExtra("PET_ID", pet.id)
                }
                startActivity(intent)
            },
            onAddPetClick = {
                startActivity(Intent(this, AddPetActivity::class.java))
            }
        )
        recyclerView.adapter = adapter

        val layoutManager = GridLayoutManager(this, 2)

        // === THE DEFINITIVE UI FIX ===
        // FIXED: The logic inside getSpanSize was incorrect.
        // We now return '1' for all items, ensuring that both the pet cards
        // and the "Add Pet" button occupy a single column, making them the same size.
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return 1 // All items should take up 1 span.
            }
        }
        recyclerView.layoutManager = layoutManager
    }
}
