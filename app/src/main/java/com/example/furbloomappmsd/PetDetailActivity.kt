package com.example.furbloomappmsd

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity // FIXED: Kept only one import
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furbloomappmsd.data.Pet
import com.example.furbloomappmsd.data.PetReminder
import com.example.furbloomappmsd.ui.EditPetActivity
import com.example.furbloomappmsd.ui.PetViewModel
import com.example.furbloomappmsd.ui.PetViewModelFactory
import com.example.furbloomappmsd.viewmodel.ReminderViewModel
import com.example.furbloomappmsd.viewmodel.ReminderViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.appbar.MaterialToolbar

class PetDetailActivity : AppCompatActivity() {

    private lateinit var ivPetPhoto: ImageView
    private lateinit var tvPetName: TextView
    private lateinit var tvPetAge: TextView
    private lateinit var tvPetSpecies: TextView
    private lateinit var tvPetGender: TextView
    private lateinit var tvMedicalHistory: TextView
    private lateinit var tvNotes: TextView
    private lateinit var fabOptions: FloatingActionButton
    private lateinit var recyclerViewReminders: RecyclerView

    private var currentPet: Pet? = null
    private var petId: Int = -1

    private lateinit var reminderAdapter: ReminderAdapter

    private val petViewModel: PetViewModel by viewModels {
        PetViewModelFactory((application as PetApplication).petRepository)
    }

    private val reminderViewModel: ReminderViewModel by viewModels {
        ReminderViewModelFactory((application as PetApplication).reminderRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_detail)

        val toolbar: MaterialToolbar = findViewById(R.id.custom_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        ivPetPhoto = findViewById(R.id.ivPetPhoto)
        tvPetName = findViewById(R.id.tvPetName)
        tvPetAge = findViewById(R.id.tvPetAge)
        tvPetSpecies = findViewById(R.id.tvPetSpecies)
        tvPetGender = findViewById(R.id.tvPetGender)
        tvMedicalHistory = findViewById(R.id.tvMedicalHistory)
        tvNotes = findViewById(R.id.tvNotes)
        fabOptions = findViewById(R.id.fab_options)
        recyclerViewReminders = findViewById(R.id.recyclerView_pet_reminders)

        petId = intent.getIntExtra("PET_ID", -1)
        if (petId == -1) {
            Toast.makeText(this, "Error: Pet not found", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupReminderRecyclerView()
        observePetDetails()
        observeReminders()

        fabOptions.setOnClickListener { view ->
            currentPet?.let { showPetOptionsMenu(it, view) }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        // Re-observing ensures data is fresh if you come back to this screen
        observePetDetails()
        observeReminders()
    }

    private fun populateUI(pet: Pet) {
        tvPetName.text = pet.name
        tvPetAge.text = "Age: ${pet.age?.toString() ?: "N/A"}"
        tvPetSpecies.text = "Species: ${pet.species ?: "N/A"}"
        tvPetGender.text = "Gender: ${pet.gender ?: "N/A"}"
        tvMedicalHistory.text = pet.medicalHistory ?: "None provided"
        tvNotes.text = pet.notes ?: "None provided"

        pet.photoUri?.let {
            ivPetPhoto.setImageURI(Uri.parse(it))
        } ?: ivPetPhoto.setImageResource(R.drawable.ic_pet_placeholder)
    }

    private fun observePetDetails() {
        petViewModel.getPetById(petId).observe(this, Observer { pet ->
            pet?.let {
                currentPet = it
                populateUI(it)
                supportActionBar?.title = it.name // Set title to pet's name
            }
        })
    }

    private fun setupReminderRecyclerView() {
        // FIXED: Added the required 'showPetName' parameter to the adapter constructor.
        reminderAdapter = ReminderAdapter(
            showPetName = false, // We are on a pet-specific screen, so hide the name.
            onItemClick = { /* Can add an action here if needed */ },
            onToggleComplete = { reminder -> reminderViewModel.update(reminder.copy(isCompleted = !reminder.isCompleted)) },
            onDelete = { reminder -> reminderViewModel.delete(reminder) }
        )
        recyclerViewReminders.adapter = reminderAdapter
        recyclerViewReminders.layoutManager = LinearLayoutManager(this)
    }

    private fun observeReminders() {
        reminderViewModel.getRemindersForPet(petId).observe(this, Observer { reminders ->
            val sortedList = reminders.sortedWith(
                compareBy<PetReminder> { it.isCompleted }.thenBy { it.dateTime }
            )
            reminderAdapter.submitList(sortedList)
        })
    }

    private fun showPetOptionsMenu(pet: Pet, anchorView: View) {
        val popup = PopupMenu(this, anchorView)
        popup.menuInflater.inflate(R.menu.pet_options_menu, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_edit_pet -> {
                    val intent = Intent(this, EditPetActivity::class.java).apply {
                        putExtra("PET_ID", pet.id)
                    }
                    startActivity(intent)
                    true
                }
                R.id.menu_add_reminder -> {
                    val intent = Intent(this, AddEditReminderActivity::class.java).apply {
                        putExtra("PET_ID", pet.id)
                        putExtra("PET_NAME", pet.name)
                    }
                    startActivity(intent)
                    true
                }
                R.id.menu_delete_pet -> {
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
                petViewModel.deletePet(pet)
                Toast.makeText(this, "${pet.name} deleted", Toast.LENGTH_SHORT).show()
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
