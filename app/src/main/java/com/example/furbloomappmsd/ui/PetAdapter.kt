package com.example.furbloomappmsd.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.furbloomappmsd.R
import com.example.furbloomappmsd.data.Pet

// FIXED: The adapter now ONLY handles Pet items, which is much simpler and more stable.
class PetAdapter(
    private val onPetClick: (Pet) -> Unit
) : ListAdapter<Pet, PetAdapter.PetViewHolder>(PetDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pet_item, parent, false)
        return PetViewHolder(view)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        val pet = getItem(position)
        holder.bind(pet)
    }

    inner class PetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val petName: TextView = view.findViewById(R.id.tv_pet_name)
        private val petImage: ImageView = view.findViewById(R.id.iv_pet_image)

        fun bind(pet: Pet) {
            petName.text = pet.name
            pet.photoUri?.let { uri ->
                petImage.setImageURI(Uri.parse(uri))
            } ?: petImage.setImageResource(R.drawable.ic_pet_placeholder)
            itemView.setOnClickListener { onPetClick(pet) }
        }
    }
}

class PetDiffCallback : DiffUtil.ItemCallback<Pet>() {
    override fun areItemsTheSame(oldItem: Pet, newItem: Pet): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Pet, newItem: Pet): Boolean {
        return oldItem == newItem
    }
}
