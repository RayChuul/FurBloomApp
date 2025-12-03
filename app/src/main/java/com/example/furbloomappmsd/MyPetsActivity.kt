package com.example.furbloomappmsd

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MyPetsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PetAdapter
    private val petList = listOf(
        Pet("Teddy", R.drawable.ic_pet_placeholder),
        Pet("Bella", R.drawable.ic_pet_placeholder),
        Pet("Max", R.drawable.ic_pet_placeholder)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_pets)

        recyclerView = findViewById(R.id.recyclerView_pets)
        adapter = PetAdapter(petList, onAddReminderClick = { pet ->
            // Go to Add Reminder page with selected pet
            val intent = Intent(this, AddEditReminderActivity::class.java)
            intent.putExtra("PET_NAME", pet.name)
            startActivity(intent)
        })

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}
