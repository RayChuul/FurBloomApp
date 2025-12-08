package com.example.furbloomappmsd

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furbloomappmsd.data.PetReminder
import com.example.furbloomappmsd.ui.ReminderViewModel
import com.example.furbloomappmsd.ui.ReminderViewModelFactory
import com.google.android.material.appbar.MaterialToolbar

class RecyclingListActivity : AppCompatActivity() {

    private val viewModel: ReminderViewModel by viewModels {
        ReminderViewModelFactory((application as PetApplication).reminderRepository)
    }
    private lateinit var adapter: ReminderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycling_list)

        // FIXED: Set up the custom toolbar
        val toolbar: MaterialToolbar = findViewById(R.id.custom_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Completed Reminders"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_recycling)
        adapter = ReminderAdapter(
            onItemClick = { /* Optional: Open for viewing */ },
            onToggleComplete = { reminder ->
                viewModel.update(reminder.copy(isCompleted = !reminder.isCompleted))
            },
            onDelete = { reminder ->
                viewModel.delete(reminder)
            }
        )

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.allReminders.observe(this, Observer { reminders ->
            val completedReminders: List<PetReminder> = reminders.filter { it.isCompleted }
            adapter.submitList(completedReminders)
        })
    }

    // FIXED: Added this function to make the back arrow work
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
