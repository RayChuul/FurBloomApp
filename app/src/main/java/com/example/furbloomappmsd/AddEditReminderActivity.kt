package com.example.furbloomappmsd

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furbloomappmsd.data.PetReminder
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AddEditReminderActivity : AppCompatActivity() {

    private lateinit var ivPetImage: ImageView
    private lateinit var tvPetName: TextView
    private lateinit var recyclerViewReminders: RecyclerView
    private lateinit var fabOptions: FloatingActionButton
    private lateinit var reminderAdapter: ReminderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_reminder)

        ivPetImage = findViewById(R.id.iv_pet_image)
        tvPetName = findViewById(R.id.tv_pet_name)
        recyclerViewReminders = findViewById(R.id.recyclerView_reminders)
        fabOptions = findViewById(R.id.fab_options)

        // Load pet info from intent
        val petName = intent.getStringExtra("PET_NAME") ?: "Unknown Pet"
        tvPetName.text = petName
        ivPetImage.setImageResource(R.drawable.ic_pet_placeholder)

        // Initialize adapter with empty list and lambda handlers
        reminderAdapter = ReminderAdapter(
            onItemClick = { reminder ->
                Toast.makeText(this, "Clicked: ${reminder.description}", Toast.LENGTH_SHORT).show()
                // TODO: Open edit reminder popup
            },
            onToggleComplete = { reminder ->
                Toast.makeText(this, "Toggled: ${reminder.description}", Toast.LENGTH_SHORT).show()
                // TODO: Update completion status
            },
            onDelete = { reminder ->
                Toast.makeText(this, "Deleted: ${reminder.description}", Toast.LENGTH_SHORT).show()
                // TODO: Delete reminder from database
            }
        )

        // Setup RecyclerView
        recyclerViewReminders.adapter = reminderAdapter
        recyclerViewReminders.layoutManager = LinearLayoutManager(this)

        // Initially submit empty list (can replace with actual reminders from DB)
        reminderAdapter.submitList(emptyList<PetReminder>())

        // Setup FAB menu
        fabOptions.setOnClickListener { showFabMenu() }
    }

    private fun showFabMenu() {
        val popup = PopupMenu(this, fabOptions)
        popup.menu.add("Edit Pet")
        popup.menu.add("Add/Edit Reminders")
        popup.menu.add("Delete Pet")

        popup.setOnMenuItemClickListener { item ->
            when (item.title) {
                "Edit Pet" -> {
                    Toast.makeText(this, "Edit Pet clicked", Toast.LENGTH_SHORT).show()
                    // TODO: Open EditPetActivity
                    true
                }
                "Add/Edit Reminders" -> {
                    Toast.makeText(this, "Add/Edit Reminders clicked", Toast.LENGTH_SHORT).show()
                    // TODO: Open Add/Edit Reminder popup
                    true
                }
                "Delete Pet" -> {
                    Toast.makeText(this, "Delete Pet clicked", Toast.LENGTH_SHORT).show()
                    // TODO: Confirm and delete pet
                    true
                }
                else -> false
            }
        }
        popup.show()
    }
}
