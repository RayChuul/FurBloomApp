package com.example.furbloomappmsd

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furbloomappmsd.data.PetReminder
import com.example.furbloomappmsd.viewmodel.PetReminderViewModel


class RecyclingListActivity : AppCompatActivity() {

    private lateinit var viewModel: PetReminderViewModel
    private lateinit var adapter: ReminderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycling_list)

        supportActionBar?.title = "Completed Reminders"

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[PetReminderViewModel::class.java]

        // Setup RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_recycling)
        adapter = ReminderAdapter(
            onItemClick = { reminder ->
                // Optional: Open for viewing/editing
            },
            onToggleComplete = { reminder ->
                // Toggle back to pending if desired
                viewModel.toggleCompletion(reminder)
            },
            onDelete = { reminder ->
                viewModel.delete(reminder)
            }
        )

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Observe completed reminders
        viewModel.pendingReminders.observe(this) { reminders ->
            val completedReminders: List<PetReminder> = reminders.filter { it.isCompleted }
            adapter.submitList(completedReminders)
        }
    }
}
