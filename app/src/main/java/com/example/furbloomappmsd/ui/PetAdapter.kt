package com.example.furbloomappmsd.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.furbloomappmsd.R
import com.example.furbloomappmsd.data.Pet

class PetAdapter(
    private var pets: MutableList<Pet>,
    private val onPetClick: (Pet) -> Unit,
    private val onAddPetClick: () -> Unit,
    // ADD THIS LAMBDA FOR THE NEW BUTTON
    private val onPetOptionsClick: (Pet, View) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_PET = 0
    private val TYPE_ADD = 1

    override fun getItemViewType(position: Int): Int {
        return if (position < pets.size) TYPE_PET else TYPE_ADD
    }

    override fun getItemCount(): Int = pets.size + 1

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
            holder.bind(pet)
        } else if (holder is AddPetViewHolder) {
            holder.itemView.setOnClickListener { onAddPetClick() }
        }
    }

    fun updatePets(newPets: List<Pet>) {
        this.pets.clear()
        this.pets.addAll(newPets)
        notifyDataSetChanged()
    }

    inner class PetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val petName: TextView = view.findViewById(R.id.tv_pet_name)
        private val petImage: ImageView = view.findViewById(R.id.iv_pet_image)
        // FIND THE NEW BUTTON
        private val optionsButton: ImageButton = view.findViewById(R.id.btn_pet_options)

        fun bind(pet: Pet) {
            petName.text = pet.name

            pet.photoUri?.let { uri ->
                petImage.setImageURI(Uri.parse(uri))
            } ?: petImage.setImageResource(R.drawable.ic_pet_placeholder)

            // Set listeners
            itemView.setOnClickListener { onPetClick(pet) }
            optionsButton.setOnClickListener { onPetOptionsClick(pet, optionsButton) }
        }
    }

    inner class AddPetViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
