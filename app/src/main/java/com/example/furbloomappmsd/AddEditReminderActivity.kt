package com.example.furbloomappmsd

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity // This is the only one needed
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furbloomappmsd.data.PetReminder
import com.example.furbloomappmsd.viewmodel.ReminderViewModel
import com.example.furbloomappmsd.viewmodel.ReminderViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Calendar
// import androidx.appcompat.app.AppCompatActivity // FIXED: Removed the duplicate import
import com.google.android.material.appbar.MaterialToolbar

class AddEditReminderActivity : AppCompatActivity() {

    private lateinit var tvHeader: TextView
    private lateinit var recyclerViewReminders: RecyclerView
    private lateinit var fabAddReminder: FloatingActionButton
    private lateinit var reminderAdapter: ReminderAdapter

    private var petId: Int = -1
    private var petName: String = "Your Pet"

    private val viewModel: ReminderViewModel by viewModels {
        ReminderViewModelFactory((application as PetApplication).reminderRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_reminder)

        val toolbar: MaterialToolbar = findViewById(R.id.custom_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Pet Reminders"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        tvHeader = findViewById(R.id.tv_header)
        recyclerViewReminders = findViewById(R.id.recyclerView_reminders)
        fabAddReminder = findViewById(R.id.fab_add_reminder)

        petId = intent.getIntExtra("PET_ID", -1)
        petName = intent.getStringExtra("PET_NAME") ?: "Your Pet"
        tvHeader.text = "Reminders for $petName"

        if (petId == -1) {
            Toast.makeText(this, "Error: No Pet ID provided.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupRecyclerView()
        observeReminders()

        fabAddReminder.setOnClickListener {
            showAddEditReminderDialog(null)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupRecyclerView() {
        // FIXED: The adapter initialization now correctly includes the 'showPetName' parameter.
        reminderAdapter = ReminderAdapter(
            showPetName = false, // Since we are on a pet-specific screen, we don't need to show the name again.
            onItemClick = { reminder ->
                showAddEditReminderDialog(reminder)
            },
            onToggleComplete = { reminder ->
                viewModel.update(reminder.copy(isCompleted = !reminder.isCompleted))
            },
            onDelete = { reminder ->
                viewModel.delete(reminder)
            }
        )
        recyclerViewReminders.adapter = reminderAdapter
        recyclerViewReminders.layoutManager = LinearLayoutManager(this)
    }

    private fun observeReminders() {
        viewModel.getRemindersForPet(petId).observe(this, Observer { reminders ->
            val sortedList = reminders.sortedWith(
                compareBy<PetReminder> { it.isCompleted }.thenBy { it.dateTime }
            )
            reminderAdapter.submitList(sortedList)
        })
    }

    private fun showAddEditReminderDialog(existingReminder: PetReminder?) {
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
                        petId = petId,
                        petName = petName,
                        reminderType = "General",
                        description = description,
                        dateTime = calendar.timeInMillis,
                        repeat = selectedRepeat
                    )
                    if (existingReminder == null) {
                        viewModel.insert(reminder)
                    } else {
                        viewModel.update(reminder)
                    }
                } else {
                    Toast.makeText(this, "Description cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
