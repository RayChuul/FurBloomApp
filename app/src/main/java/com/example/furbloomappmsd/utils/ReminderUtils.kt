package com.example.furbloomappmsd.utils

import com.example.furbloomappmsd.data.PetReminder
import java.util.Calendar
import java.util.concurrent.TimeUnit

object ReminderUtils {

    /**
     * Expands a list of reminders to include their future repeating occurrences.
     *
     * @param baseReminders The original list of reminders from the database.
     * @param projectionMonths The number of months into the future to project repetitions.
     * @return A new list containing original and all virtual future reminders.
     */
    fun getExpandedReminders(baseReminders: List<PetReminder>, projectionMonths: Int = 3): List<PetReminder> {
        val expandedList = mutableListOf<PetReminder>()
        val projectionLimit = Calendar.getInstance().apply { add(Calendar.MONTH, projectionMonths) }

        baseReminders.forEach { reminder ->
            // Always add the original reminder
            expandedList.add(reminder)

            if (reminder.repeat != "None") {
                val cursor = Calendar.getInstance().apply { timeInMillis = reminder.dateTime }

                when (reminder.repeat) {
                    "Daily" -> {
                        // Add daily occurrences from the day after the original, up to the limit
                        cursor.add(Calendar.DAY_OF_YEAR, 1)
                        while (cursor.before(projectionLimit)) {
                            // Create a virtual reminder with the new date/time
                            expandedList.add(reminder.copy(id = -1, dateTime = cursor.timeInMillis, isCompleted = false))
                            cursor.add(Calendar.DAY_OF_YEAR, 1)
                        }
                    }
                    "Weekly" -> {
                        // Add weekly occurrences
                        cursor.add(Calendar.WEEK_OF_YEAR, 1)
                        while (cursor.before(projectionLimit)) {
                            expandedList.add(reminder.copy(id = -1, dateTime = cursor.timeInMillis, isCompleted = false))
                            cursor.add(Calendar.WEEK_OF_YEAR, 1)
                        }
                    }
                    "Monthly" -> {
                        // Add monthly occurrences
                        cursor.add(Calendar.MONTH, 1)
                        while (cursor.before(projectionLimit)) {
                            expandedList.add(reminder.copy(id = -1, dateTime = cursor.timeInMillis, isCompleted = false))
                            cursor.add(Calendar.MONTH, 1)
                        }
                    }
                }
            }
        }
        return expandedList
    }

    /**
     * Checks if two Calendar instances represent the same day.
     */
    fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}
