package com.example.furbloomappmsd

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PetAdapter(
    private val pets: List<Pet>,
    private val onAddReminderClick: (Pet) -> Unit
) : RecyclerView.Adapter<PetAdapter.PetViewHolder>() {

    inner class PetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val petName: TextView = view.findViewById(R.id.tv_pet_name)
        val petImage: ImageView = view.findViewById(R.id.iv_pet_image)
        val btnAddReminder: Button = view.findViewById(R.id.btn_add_reminder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pet_item, parent, false)
        return PetViewHolder(view)
    }

    override fun getItemCount(): Int = pets.size

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        val pet = pets[position]
        holder.petName.text = pet.name
        holder.petImage.setImageResource(pet.imageResId)
        holder.btnAddReminder.setOnClickListener {
            onAddReminderClick(pet)
        }
    }
}
