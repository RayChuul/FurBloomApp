package com.example.furbloomappmsd

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PetAdapter(
    private val pets: List<Pet>,
    private val onPetClick: (Pet) -> Unit,
    private val onAddPetClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // View type constants
    private val TYPE_PET = 0
    private val TYPE_ADD = 1

    override fun getItemViewType(position: Int): Int {
        return if (position < pets.size) TYPE_PET else TYPE_ADD
    }

    override fun getItemCount(): Int = pets.size + 1 // +1 for "Add Pet" card

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_PET) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.pet_item, parent, false)
            PetViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.pet_add_item, parent, false)
            AddPetViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PetViewHolder) {
            val pet = pets[position]
            holder.petName.text = pet.name
            holder.petImage.setImageResource(pet.imageResId)
            holder.itemView.setOnClickListener { onPetClick(pet) }
        } else if (holder is AddPetViewHolder) {
            holder.itemView.setOnClickListener { onAddPetClick() }
        }
    }

    inner class PetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val petName: TextView = view.findViewById(R.id.tv_pet_name)
        val petImage: ImageView = view.findViewById(R.id.iv_pet_image)
    }

    inner class AddPetViewHolder(view: View) : RecyclerView.ViewHolder(view)
}

