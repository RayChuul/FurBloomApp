package com.example.furbloomappmsd

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.listeners.OnCalendarDayClickListener
import com.example.furbloomappmsd.data.PetReminder
import com.example.furbloomappmsd.utils.ReminderUtils
import com.example.furbloomappmsd.viewmodel.ReminderViewModel
import com.example.furbloomappmsd.viewmodel.ReminderViewModelFactory
import java.util.*

class CalendarFragment : Fragment() {

    private val viewModel: ReminderViewModel by activityViewModels {
        ReminderViewModelFactory((requireActivity().application as PetApplication).reminderRepository)
    }

    private lateinit var calendarView: CalendarView
    private lateinit var reminderAdapter: ReminderAdapter
    private var expandedReminders: List<PetReminder> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        calendarView = view.findViewById(R.id.calendarView)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView_calendar_reminders)

        setupRecyclerView(recyclerView)
        observeReminders()

        calendarView.setOnCalendarDayClickListener(object : OnCalendarDayClickListener {
            override fun onClick(calendarDay: CalendarDay) {
                updateReminderListForDay(calendarDay.calendar)
            }
        })

        return view
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        // FIXED: Changed the empty lambda {} to null, which was causing a fatal ClassCastException.
        reminderAdapter = ReminderAdapter(
            showPetName = true,
            onItemClick = null, // In the calendar, items are not clickable for editing.
            onToggleComplete = { reminder -> if (reminder.id != -1) viewModel.update(reminder.copy(isCompleted = !reminder.isCompleted)) },
            onDelete = { reminder -> if (reminder.id != -1) viewModel.delete(reminder) }
        )
        recyclerView.adapter = reminderAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun observeReminders() {
        viewModel.allReminders.observe(viewLifecycleOwner, Observer { baseReminders ->
            this.expandedReminders = ReminderUtils.getExpandedReminders(baseReminders)
            updateCalendarEvents()
            val today = calendarView.selectedDates.firstOrNull() ?: Calendar.getInstance()
            updateReminderListForDay(today)
        })
    }

    private fun updateCalendarEvents() {
        val eventDays = mutableListOf<CalendarDay>()
        expandedReminders.forEach { reminder ->
            val calendar = Calendar.getInstance().apply { timeInMillis = reminder.dateTime }
            eventDays.add(CalendarDay(calendar).apply { imageResource = R.drawable.ic_dot })
        }
        calendarView.setCalendarDays(eventDays)
    }

    private fun updateReminderListForDay(day: Calendar) {
        val remindersForDay = expandedReminders.filter {
            val reminderCalendar = Calendar.getInstance().apply { timeInMillis = it.dateTime }
            ReminderUtils.isSameDay(reminderCalendar, day)
        }
        reminderAdapter.submitList(remindersForDay.sortedBy { it.dateTime })
    }
}
