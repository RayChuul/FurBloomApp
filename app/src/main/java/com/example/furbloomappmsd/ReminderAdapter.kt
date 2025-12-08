package com.example.furbloomappmsd

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.furbloomappmsd.data.PetReminder
import java.text.SimpleDateFormat
import java.util.*

class ReminderAdapter(
    private val showPetName: Boolean = false,
    private val onItemClick: ((PetReminder) -> Unit)? = null,
    private val onToggleComplete: (PetReminder) -> Unit,
    private val onDelete: (PetReminder) -> Unit
) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    private var reminders: List<PetReminder> = emptyList()

    fun submitList(newReminders: List<PetReminder>) {
        reminders = newReminders
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reminder, parent, false)
        // Pass the listeners from the adapter to the ViewHolder
        return ReminderViewHolder(view, showPetName, onItemClick, onToggleComplete, onDelete)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.bind(reminders[position])
    }

    override fun getItemCount(): Int = reminders.size

    // The ViewHolder now correctly receives and uses the listeners passed from the adapter.
    class ReminderViewHolder(
        itemView: View,
        private val showPetName: Boolean,
        private val onItemClick: ((PetReminder) -> Unit)?,
        private val onToggleComplete: (PetReminder) -> Unit,
        private val onDelete: (PetReminder) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvDescription: TextView = itemView.findViewById(R.id.tv_reminder_description)
        private val tvTime: TextView = itemView.findViewById(R.id.tv_reminder_time)
        private val cbCompleted: CheckBox = itemView.findViewById(R.id.cb_completed)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete)

        fun bind(reminder: PetReminder) {
            tvDescription.text = if (showPetName) {
                "${reminder.petName}: ${reminder.description}"
            } else {
                reminder.description
            }

            val timeFormat = SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault())
            tvTime.text = timeFormat.format(Date(reminder.dateTime))

            cbCompleted.setOnCheckedChangeListener(null) // Avoid triggering listener during bind
            cbCompleted.isChecked = reminder.isCompleted

            if (reminder.isCompleted) {
                tvDescription.paintFlags = tvDescription.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                tvDescription.paintFlags = tvDescription.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            val isRealReminder = reminder.id != -1
            cbCompleted.isEnabled = isRealReminder
            btnDelete.visibility = if (isRealReminder) View.VISIBLE else View.INVISIBLE

            if (isRealReminder) {
                // Set listeners for interaction
                cbCompleted.setOnCheckedChangeListener { _, _ -> onToggleComplete(reminder) }
                btnDelete.setOnClickListener { onDelete(reminder) }

                // Only set the item click listener if the handler is not null
                onItemClick?.let { clicker ->
                    itemView.setOnClickListener { clicker(reminder) }
                } ?: itemView.setOnClickListener(null)
            } else {
                // Virtual reminders are not clickable
                itemView.setOnClickListener(null)
            }
        }
    }
}
