package com.example.furbloomappmsd

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furbloomappmsd.data.Pet
import com.example.furbloomappmsd.data.PetReminder
import com.example.furbloomappmsd.ui.EditPetActivity
import com.example.furbloomappmsd.ui.PetViewModel
import com.example.furbloomappmsd.ui.PetViewModelFactory
import com.example.furbloomappmsd.ui.ReminderViewModel
import com.example.furbloomappmsd.ui.ReminderViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Calendar
import java.util.concurrent.TimeUnit

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

        val toolbar: MaterialToolbar = findViewById(R.id.detail_toolbar)
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

        // FIXED: Change FAB icon to be more descriptive
        fabOptions.setImageResource(R.drawable.ic_more_vert)

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
        observePetDetails()
        observeReminders()
    }

    private fun populateUI(pet: Pet) {
        tvPetName.text = pet.name
        // FIXED: Calculate age string from birthDate
        tvPetAge.text = pet.birthDate?.let { getAgeString(it) } ?: "Age: N/A"
        tvPetSpecies.text = "Species: ${pet.species ?: "N/A"}"
        tvPetGender.text = "Gender: ${pet.gender ?: "N/A"}"
        tvMedicalHistory.text = pet.medicalHistory ?: "None provided"
        tvNotes.text = pet.notes ?: "None provided"

        pet.photoUri?.let {
            ivPetPhoto.setImageURI(Uri.parse(it))
        } ?: ivPetPhoto.setImageResource(R.drawable.ic_pet_placeholder)
    }

    // FIXED: Add a helper function to calculate age
    private fun getAgeString(birthDate: Long): String {
        val today = Calendar.getInstance()
        val birth = Calendar.getInstance().apply { timeInMillis = birthDate }

        var years = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR)
        var months = today.get(Calendar.MONTH) - birth.get(Calendar.MONTH)

        if (today.get(Calendar.DAY_OF_MONTH) < birth.get(Calendar.DAY_OF_MONTH)) {
            months--
        }
        if (months < 0) {
            years--
            months += 12
        }

        return when {
            years > 0 -> "Age: $years year${if (years > 1) "s" else ""}, $months month${if (months != 1) "s" else ""}"
            months > 0 -> "Age: $months month${if (months > 1) "s" else ""}"
            else -> "Age: Less than a month old"
        }
    }

    private fun observePetDetails() {
        petViewModel.getPetById(petId).observe(this, Observer { pet ->
            pet?.let {
                currentPet = it
                populateUI(it)
                supportActionBar?.title = it.name
            }
        })
    }

    private fun setupReminderRecyclerView() {
        reminderAdapter = ReminderAdapter(
            showPetName = false,
            // FIXED: Clicking a reminder now opens the edit dialog
            onItemClick = { reminder ->
                currentPet?.let { showAddEditReminderDialog(it, reminder) }
            },
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
                // FIXED: "Add Reminder" now directly opens the dialog
                R.id.menu_add_reminder -> {
                    showAddEditReminderDialog(pet, null)
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

    // FIXED: Moved the dialog logic here from AddEditReminderActivity
    private fun showAddEditReminderDialog(pet: Pet, existingReminder: PetReminder?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_reminder, null)
        val etReminderDescription = dialogView.findViewById<EditText>(R.id.et_reminder_description)
        val btnSetDate = dialogView.findViewById<Button>(R.id.btn_set_date)
        val btnSetTime = dialogView.findViewById<Button>(R.id.btn_set_time)
        val spinnerRepeat = dialogView.findViewById<Spinner>(R.id.spinner_repeat)

        val repeatOptions = arrayOf("None", "Daily", "Weekly", "Monthly")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, repeatOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRepeat.adapter = spinnerAdapter

        val calendar = Calendar.getInstance()
        if (existingReminder != null) {
            etReminderDescription.setText(existingReminder.description)
            calendar.timeInMillis = existingReminder.dateTime
            val repeatIndex = repeatOptions.indexOf(existingReminder.repeat)
            spinnerRepeat.setSelection(if (repeatIndex != -1) repeatIndex else 0)
        }

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            btnSetDate.text = "$dayOfMonth/${month + 1}/$year"
        }

        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            btnSetTime.text = String.format("%02d:%02d", hour, minute)
        }

        btnSetDate.setOnClickListener {
            DatePickerDialog(this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        btnSetTime.setOnClickListener {
            TimePickerDialog(this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }

        AlertDialog.Builder(this)
            .setTitle(if (existingReminder == null) "Add Reminder" else "Edit Reminder")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val description = etReminderDescription.text.toString().trim()
                val selectedRepeat = spinnerRepeat.selectedItem.toString()

                if (description.isNotEmpty()) {
                    val reminder = existingReminder?.copy(
                        description = description,
                        dateTime = calendar.timeInMillis,
                        repeat = selectedRepeat
                    ) ?: PetReminder(
                        petId = pet.id,
                        petName = pet.name,
                        reminderType = "General",
                        description = description,
                        dateTime = calendar.timeInMillis,
                        repeat = selectedRepeat
                    )
                    if (existingReminder == null) {
                        reminderViewModel.insert(reminder)
                    } else {
                        reminderViewModel.update(reminder)
                    }
                } else {
                    Toast.makeText(this, "Description cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
