package com.example.furbloomappmsd

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furbloomappmsd.data.PetReminderDatabase
import com.example.furbloomappmsd.viewmodel.PetReminderViewModel
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: PetReminderViewModel
    private lateinit var adapter: ReminderAdapter
    private lateinit var database: PetReminderDatabase
    private lateinit var tvNextReminder: TextView
    private lateinit var tvNextReminderTime: TextView
    private lateinit var cardReminder: MaterialCardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        database = PetReminderDatabase.getDatabase(this)
        viewModel = ViewModelProvider(this)[PetReminderViewModel::class.java]

        // Initialize views
        cardReminder = findViewById(R.id.card_next_reminder)
        tvNextReminder = findViewById(R.id.tv_next_reminder)
        tvNextReminderTime = findViewById(R.id.tv_next_reminder_time)

        // Observe today's reminders
        viewModel.pendingReminders.observe(this) { reminders ->

            val today = Calendar.getInstance()
            today.set(Calendar.HOUR_OF_DAY, 0)
            today.set(Calendar.MINUTE, 0)
            today.set(Calendar.SECOND, 0)
            val startOfDay = today.timeInMillis

            today.set(Calendar.HOUR_OF_DAY, 23)
            today.set(Calendar.MINUTE, 59)
            today.set(Calendar.SECOND, 59)
            val endOfDay = today.timeInMillis

            val todayReminders = reminders.filter { it.dateTime in startOfDay..endOfDay }
            adapter.submitList(todayReminders)

            if (todayReminders.isNotEmpty()) {
                // Show the next reminder
                val nextReminder = todayReminders.first()
                tvNextReminder.text = nextReminder.description
                val timeFormat = SimpleDateFormat("'Today at' h:mm a", Locale.getDefault())
                tvNextReminderTime.text = timeFormat.format(Date(nextReminder.dateTime))
            } else {
                // No reminders today → show "Done for the Day!"
                tvNextReminder.text = "Done for the Day!"
                tvNextReminderTime.text = ""
            }

            cardReminder.visibility = View.VISIBLE
        }

        // Setup RecyclerView for Daily List
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_daily_list)
        adapter = ReminderAdapter(
            onItemClick = { reminder ->
                val intent = Intent(this, AddEditReminderActivity::class.java)
                intent.putExtra("REMINDER_ID", reminder.id)
                startActivity(intent)
            },
            onToggleComplete = { reminder ->
                viewModel.toggleCompletion(reminder)
            },
            onDelete = { reminder ->
                viewModel.delete(reminder)
            }
        )

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.pendingReminders.observe(this) { reminders ->
            // Filter today's reminders
            val today = Calendar.getInstance()
            today.set(Calendar.HOUR_OF_DAY, 0)
            today.set(Calendar.MINUTE, 0)
            today.set(Calendar.SECOND, 0)
            val startOfDay = today.timeInMillis

            today.set(Calendar.HOUR_OF_DAY, 23)
            today.set(Calendar.MINUTE, 59)
            today.set(Calendar.SECOND, 59)
            val endOfDay = today.timeInMillis

            val todayReminders = reminders.filter { it.dateTime in startOfDay..endOfDay }
            adapter.submitList(todayReminders)

            if (todayReminders.isNotEmpty()) {
                // Show the next upcoming reminder
                val nextReminder = todayReminders.first()
                tvNextReminder.text = nextReminder.description
                val timeFormat = SimpleDateFormat("'Today at' h:mm a", Locale.getDefault())
                tvNextReminderTime.text = timeFormat.format(Date(nextReminder.dateTime))
            } else {
                // No reminders → show "Done for the Day!"
                tvNextReminder.text = "✅ Done for the Day!"
                tvNextReminderTime.text = ""
            }

            // Always make the card visible
            cardReminder.visibility = View.VISIBLE
        }


        // Navigation buttons
        findViewById<MaterialCardView>(R.id.btn_my_pets).setOnClickListener {
            startActivity(Intent(this, MyPetsActivity::class.java))
        }

        // Bottom navigation
        findViewById<View>(R.id.nav_home).setOnClickListener {
            // Already on home
        }

        findViewById<View>(R.id.nav_calendar).setOnClickListener {
            startActivity(Intent(this, CalendarActivity::class.java))
        }

        findViewById<View>(R.id.nav_settings).setOnClickListener {
            startActivity(Intent(this, RecyclingListActivity::class.java))
        }
    }
}