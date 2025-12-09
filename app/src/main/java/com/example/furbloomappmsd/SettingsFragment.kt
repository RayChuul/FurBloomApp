package com.example.furbloomappmsd

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Find all the new UI elements
        val notificationSwitch: SwitchMaterial = view.findViewById(R.id.switch_notifications)
        val darkModeLayout: LinearLayout = view.findViewById(R.id.setting_dark_mode)
        val darkModeStatusText: TextView = view.findViewById(R.id.tv_dark_mode_status)
        val appVersionText: TextView = view.findViewById(R.id.tv_app_version)
        val clearDataButton: TextView = view.findViewById(R.id.btn_clear_data)

        // --- Logic for Notifications ---
        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(context, "Notifications Enabled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Notifications Disabled", Toast.LENGTH_SHORT).show()
            }
        }

        // --- Logic for Dark Mode ---
        // Set initial status text
        darkModeStatusText.text = when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_NO -> "Light"
            AppCompatDelegate.MODE_NIGHT_YES -> "Dark"
            else -> "System default"
        }

        darkModeLayout.setOnClickListener {
            showDarkModeDialog()
        }

        // --- Logic for App Version ---
        try {
            val versionName = requireContext().packageManager
                .getPackageInfo(requireContext().packageName, 0).versionName
            appVersionText.text = versionName
        } catch (e: Exception) {
            appVersionText.text = "N/A"
        }


        // --- Logic for Clearing Data ---
        clearDataButton.setOnClickListener {
            showClearDataConfirmationDialog()
        }

        return view
    }

    private fun showDarkModeDialog() {
        val options = arrayOf("Light", "Dark", "System default")
        val currentNightMode = AppCompatDelegate.getDefaultNightMode()
        val checkedItem = when (currentNightMode) {
            AppCompatDelegate.MODE_NIGHT_NO -> 0
            AppCompatDelegate.MODE_NIGHT_YES -> 1
            else -> 2
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choose theme")
            .setSingleChoiceItems(options, checkedItem) { dialog, which ->
                when (which) {
                    0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showClearDataConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Clear All Data")
            .setMessage("Are you sure you want to delete all pets and reminders? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->

                Toast.makeText(context, "All data cleared!", Toast.LENGTH_SHORT).show()

            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
