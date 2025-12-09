package com.example.furbloomappmsd.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
// FIXED: Removed the incorrect and unused Jetpack Compose import
import androidx.recyclerview.widget.RecyclerView
import com.example.furbloomappmsd.R
import com.example.furbloomappmsd.data.Pet

class PetAdapter(
    private val onPetClick: (Pet) -> Unit,
    private val onAddPetClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var pets: List<Pet> = emptyList()


    companion object {
        private const val PET_VIEW_TYPE = 1
        private const val ADD_BUTTON_VIEW_TYPE = 2
    }

    fun submitList(newPets: List<Pet>) {
        pets = newPets
        notifyDataSetChanged() // Reload the entire list
    }

    // ViewHolder for the regular pet items
    class PetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val petName: TextView = view.findViewById(R.id.tv_pet_name)
        private val petImage: ImageView = view.findViewById(R.id.iv_pet_image)

        fun bind(pet: Pet, onPetClick: (Pet) -> Unit) {
            petName.text = pet.name
            pet.photoUri?.let { uri ->
                petImage.setImageURI(Uri.parse(uri))
            } ?: petImage.setImageResource(R.drawable.ic_pet_placeholder)
            itemView.setOnClickListener { onPetClick(pet) }
        }
    }

    // ViewHolder for the "Add Pet" button
    class AddPetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(onAddPetClick: () -> Unit) {
            itemView.setOnClickListener { onAddPetClick() }
        }
    }

    override fun getItemViewType(position: Int): Int {
        // If the position is equal to the number of pets, it's the "Add" button
        return if (position < pets.size) PET_VIEW_TYPE else ADD_BUTTON_VIEW_TYPE
    }

    override fun getItemCount(): Int {
        // The total number of items is the number of pets plus the "Add" button
        return pets.size + 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == PET_VIEW_TYPE) {
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
        if (holder.itemViewType == PET_VIEW_TYPE) {
            // It's a pet, so bind pet data
            (holder as PetViewHolder).bind(pets[position], onPetClick)
        } else {
            // It's the "Add Pet" button
            (holder as AddPetViewHolder).bind(onAddPetClick)
        }
    }
}
