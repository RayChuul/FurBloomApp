package com.example.furbloomappmsd

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furbloomappmsd.data.Pet
import com.example.furbloomappmsd.ui.AddPetActivity
import com.example.furbloomappmsd.ui.EditPetActivity
import com.example.furbloomappmsd.ui.PetAdapter
import com.example.furbloomappmsd.ui.PetViewModel
import com.example.furbloomappmsd.ui.PetViewModelFactory

class MyPetsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PetAdapter

    private val viewModel: PetViewModel by viewModels {
        PetViewModelFactory((application as PetApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_pets)

        recyclerView = findViewById(R.id.recyclerView_pets)

        setupRecyclerView()

        viewModel.allPets.observe(this) { pets ->
            adapter.updatePets(pets)
        }
    }

    private fun setupRecyclerView() {
        adapter = PetAdapter(
            pets = mutableListOf(),
            onPetClick = { pet ->
                // CHANGED: Go to the reminders screen on normal click
                val intent = Intent(this, AddEditReminderActivity::class.java).apply {
                    putExtra("PET_ID", pet.id)
                    putExtra("PET_NAME", pet.name)
                    pet.photoUri?.let { putExtra("PET_PHOTO_URI", it) }
                }
                startActivity(intent)
            },
            onAddPetClick = {
                // Open Add Pet Activity
                startActivity(Intent(this, AddPetActivity::class.java))
            },
            onPetOptionsClick = { pet, view ->
                // Show the popup menu from the options button
                showPetOptionsMenu(pet, view)
            }
        )

        recyclerView.layoutManager = GridLayoutManager(this, 2).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    // Make the "Add Pet" button take up the full width
                    return if (adapter.getItemViewType(position) == 1) 2 else 1
                }
            }
        }
        recyclerView.adapter = adapter
    }

    private fun showPetOptionsMenu(pet: Pet, anchorView: View) {
        val popup = PopupMenu(this, anchorView)
        popup.menuInflater.inflate(R.menu.pet_options_menu, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_edit_pet -> {
                    // Open EditPetActivity
                    val intent = Intent(this, EditPetActivity::class.java).apply {
                        putExtra("PET_ID", pet.id)
                    }
                    startActivity(intent)
                    true
                }
                R.id.menu_add_reminder -> {
                    // Open Add/Edit Reminder Activity
                    val intent = Intent(this, AddEditReminderActivity::class.java).apply {
                        putExtra("PET_ID", pet.id)
                        putExtra("PET_NAME", pet.name)
                        pet.photoUri?.let { putExtra("PET_PHOTO_URI", it) }
                    }
                    startActivity(intent)
                    true
                }
                R.id.menu_delete_pet -> {
                    // Show a confirmation dialog before deleting
                    showDeleteConfirmationDialog(pet)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun showDeleteConfirmationDialog(pet: Pet) {
        AlertDialog.Builder(this)
            .setTitle("Delete Pet")
            .setMessage("Are you sure you want to delete ${pet.name}? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deletePet(pet)
                Toast.makeText(this, "${pet.name} deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
