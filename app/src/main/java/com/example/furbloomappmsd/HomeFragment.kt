package com.example.furbloomappmsd

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furbloomappmsd.data.PetReminder
import com.example.furbloomappmsd.utils.ReminderUtils
import com.example.furbloomappmsd.viewmodel.ReminderViewModel
import com.example.furbloomappmsd.viewmodel.ReminderViewModelFactory
import java.util.Calendar

class HomeFragment : Fragment() {

    private lateinit var dailyTasksAdapter: ReminderAdapter
    private val viewModel: ReminderViewModel by activityViewModels {
        ReminderViewModelFactory((requireActivity().application as PetApplication).reminderRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val recyclerViewDailyTasks = view.findViewById<RecyclerView>(R.id.recyclerView_daily_tasks)
        val myPetsButton = view.findViewById<Button>(R.id.btn_my_pets)

        setupRecyclerView(recyclerViewDailyTasks)
        observeDailyTasks()

        myPetsButton.setOnClickListener {
            startActivity(Intent(activity, MyPetsActivity::class.java))
        }

        return view
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        dailyTasksAdapter = ReminderAdapter(
            showPetName = true,
            // onItemClick is now optional and can be omitted.
            onToggleComplete = { reminder ->
                if (reminder.id != -1) {
                    viewModel.update(reminder.copy(isCompleted = !reminder.isCompleted))
                }
            },
            onDelete = { reminder ->
                if (reminder.id != -1) {
                    viewModel.delete(reminder)
                }
            }
        )
        recyclerView.adapter = dailyTasksAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun observeDailyTasks() {
        viewModel.allReminders.observe(viewLifecycleOwner, Observer { baseReminders ->
            val allProjectedReminders = ReminderUtils.getExpandedReminders(baseReminders)
            val today = Calendar.getInstance()

            val todayReminders = allProjectedReminders.filter {
                val reminderCalendar = Calendar.getInstance().apply { timeInMillis = it.dateTime }
                ReminderUtils.isSameDay(reminderCalendar, today)
            }

            val sortedList = todayReminders.sortedWith(
                compareBy<PetReminder> { it.isCompleted }
                    .thenBy { it.dateTime }
            )
            dailyTasksAdapter.submitList(sortedList)
        })
    }
}
