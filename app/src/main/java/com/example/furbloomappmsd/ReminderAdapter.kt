package com.example.furbloomappmsd

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.furbloomappmsd.data.PetReminder
import java.text.SimpleDateFormat
import java.util.*

class ReminderAdapter(
    private val showPetName: Boolean = false,
    private val onItemClick: (PetReminder) -> Unit,
    private val onToggleComplete: (PetReminder) -> Unit,
    private val onDelete: (PetReminder) -> Unit
) : ListAdapter<PetReminder, ReminderAdapter.ReminderViewHolder>(ReminderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reminder, parent, false)
        return ReminderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = getItem(position)
        holder.bind(reminder)
    }

    inner class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDescription: TextView = itemView.findViewById(R.id.tv_reminder_description)
        private val tvTime: TextView = itemView.findViewById(R.id.tv_reminder_time)
        private val cbCompleted: CheckBox = itemView.findViewById(R.id.cb_completed)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete)

        fun bind(reminder: PetReminder) {
            // FIXED: Logic to conditionally show the pet's name
            tvDescription.text = if (showPetName) {
                "${reminder.petName}: ${reminder.description}"
            } else {
                reminder.description
            }
            val timeFormat = SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault())
            tvTime.text = timeFormat.format(Date(reminder.dateTime))

            // FIXED: This logic correctly handles the toggle action
            cbCompleted.setOnCheckedChangeListener(null) // Prevent listener from firing during bind
            cbCompleted.isChecked = reminder.isCompleted
            cbCompleted.setOnCheckedChangeListener { _, _ ->
                onToggleComplete(reminder)
            }

            itemView.setOnClickListener { onItemClick(reminder) }
            btnDelete.setOnClickListener { onDelete(reminder) }
        }
    }

    class ReminderDiffCallback : DiffUtil.ItemCallback<PetReminder>() {
        override fun areItemsTheSame(oldItem: PetReminder, newItem: PetReminder): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PetReminder, newItem: PetReminder): Boolean {
            // Compare all relevant fields
            return oldItem == newItem
        }
    }

}


