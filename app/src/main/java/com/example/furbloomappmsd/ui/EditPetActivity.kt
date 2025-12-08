package com.example.furbloomappmsd.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.furbloomappmsd.PetApplication
import com.example.furbloomappmsd.R
import com.example.furbloomappmsd.data.Pet

class EditPetActivity : AppCompatActivity() {

    private lateinit var ivPetPhoto: ImageView
    private lateinit var etName: EditText
    private lateinit var etAge: EditText
    private lateinit var etSpecies: EditText
    private lateinit var etGender: EditText
    private lateinit var etMedicalHistory: EditText
    private lateinit var etNotes: EditText
    private lateinit var btnChoosePhoto: Button
    private lateinit var btnSave: Button

    private var currentPet: Pet? = null
    private var photoUri: String? = null
    private var petId: Int = -1

    private val viewModel: PetViewModel by viewModels {
        PetViewModelFactory((application as PetApplication).repository)
    }

    private val PICK_IMAGE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // We can reuse the same layout as AddPetActivity
        setContentView(R.layout.activity_add_pet)
        supportActionBar?.title = "Edit Pet"

        ivPetPhoto = findViewById(R.id.ivPetPhoto)
        etName = findViewById(R.id.etPetName)
        etAge = findViewById(R.id.etPetAge)
        etSpecies = findViewById(R.id.etPetSpecies)
        etGender = findViewById(R.id.etPetGender)
        etMedicalHistory = findViewById(R.id.etMedicalHistory)
        etNotes = findViewById(R.id.etNotes)
        btnChoosePhoto = findViewById(R.id.btnChoosePhoto)
        btnSave = findViewById(R.id.btnSavePet)

        // Get pet ID from the intent
        petId = intent.getIntExtra("PET_ID", -1)
        if (petId == -1) {
            Toast.makeText(this, "Error: Could not find pet.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Observe the pet data from the database
        viewModel.getPetById(petId).observe(this, Observer { pet ->
            pet?.let {
                currentPet = it
                populateUI(it)
            }
        })

        btnChoosePhoto.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(gallery, PICK_IMAGE)
        }

        btnSave.setOnClickListener {
            savePetDetails()
        }
    }

    private fun populateUI(pet: Pet) {
        etName.setText(pet.name)
        etAge.setText(pet.age?.toString() ?: "")
        etSpecies.setText(pet.species ?: "")
        etGender.setText(pet.gender ?: "")
        etMedicalHistory.setText(pet.medicalHistory ?: "")
        etNotes.setText(pet.notes ?: "")

        photoUri = pet.photoUri
        photoUri?.let {
            ivPetPhoto.setImageURI(Uri.parse(it))
        } ?: ivPetPhoto.setImageResource(R.drawable.ic_pet_placeholder)
    }

    private fun savePetDetails() {
        val name = etName.text.toString().trim()
        if (name.isEmpty()) {
            Toast.makeText(this, "Pet name is required", Toast.LENGTH_SHORT).show()
            return
        }

        val age = etAge.text.toString().toIntOrNull()
        val species = etSpecies.text.toString().trim()
        val gender = etGender.text.toString().trim()
        val medicalHistory = etMedicalHistory.text.toString().trim()
        val notes = etNotes.text.toString().trim()

        val updatedPet = currentPet?.copy(
            name = name,
            age = age,
            species = if (species.isEmpty()) null else species,
            gender = if (gender.isEmpty()) null else gender,
            medicalHistory = if (medicalHistory.isEmpty()) null else medicalHistory,
            notes = if (notes.isEmpty()) null else notes,
            photoUri = photoUri // This is updated from onActivityResult
        )

        updatedPet?.let {
            viewModel.updatePet(it)
            Toast.makeText(this, "Pet details updated!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data
            imageUri?.let {
                ivPetPhoto.setImageURI(it)
                photoUri = it.toString() // Update the URI to be saved
            }
        }
    }
}
