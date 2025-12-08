package com.example.furbloomappmsd.ui

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView // FIXED: Changed from Button to TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.furbloomappmsd.PetApplication
import com.example.furbloomappmsd.R
import com.example.furbloomappmsd.data.Pet
import com.google.android.material.appbar.MaterialToolbar
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EditPetActivity : AppCompatActivity() {

    private lateinit var ivPetPhoto: ImageView
    private lateinit var etName: EditText
    // === THE CRITICAL FIX ===
    // The layout uses a TextView for this button now. This must be a TextView.
    private lateinit var btnSetBirthDate: TextView
    private lateinit var etSpecies: EditText
    private lateinit var spinnerGender: Spinner
    private lateinit var etMedicalHistory: EditText
    private lateinit var etNotes: EditText
    private lateinit var btnChoosePhoto: Button
    private lateinit var btnSave: Button

    private var currentPet: Pet? = null
    private var photoUri: String? = null
    private var birthDate: Long? = null
    private var petId: Int = -1

    private val viewModel: PetViewModel by viewModels {
        PetViewModelFactory((application as PetApplication).petRepository)
    }

    private val genderOptions = arrayOf("Unknown", "Male", "Female")

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                ivPetPhoto.setImageURI(uri)
                photoUri = uri.toString()
            }
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data
            if (imageUri != null) {
                ivPetPhoto.setImageURI(imageUri)
                photoUri = imageUri.toString()
            } else {
                val bitmap = result.data?.extras?.get("data") as? Bitmap
                bitmap?.let {
                    ivPetPhoto.setImageBitmap(it)
                    photoUri = saveBitmapAndGetUri(it).toString()
                }
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                launchCamera()
            } else {
                Toast.makeText(this, "Camera permission is required to take photos.", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_pet) // This correctly uses the same layout

        val toolbar: MaterialToolbar = findViewById(R.id.custom_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Edit Pet"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        ivPetPhoto = findViewById(R.id.ivPetPhoto)
        etName = findViewById(R.id.etPetName)
        btnSetBirthDate = findViewById(R.id.btnSetBirthDate) // This now correctly finds the TextView
        etSpecies = findViewById(R.id.etPetSpecies)
        spinnerGender = findViewById(R.id.spinnerPetGender)
        etMedicalHistory = findViewById(R.id.etMedicalHistory)
        etNotes = findViewById(R.id.etNotes)
        btnChoosePhoto = findViewById(R.id.btnChoosePhoto)
        btnSave = findViewById(R.id.btnSavePet)

        setupGenderSpinner()

        btnSetBirthDate.setOnClickListener {
            showDatePickerDialog()
        }

        petId = intent.getIntExtra("PET_ID", -1)
        if (petId == -1) {
            Toast.makeText(this, "Error: Could not find pet.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        viewModel.getPetById(petId).observe(this, Observer { pet ->
            pet?.let {
                if (currentPet == null) { // Only populate once to avoid overwriting user changes
                    currentPet = it
                    populateUI(it)
                }
            }
        })

        btnChoosePhoto.setOnClickListener {
            showImageSourceDialog()
        }

        btnSave.setOnClickListener {
            savePetDetails()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Choose from Gallery", "Open Camera")
        AlertDialog.Builder(this)
            .setTitle("Choose a photo")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        galleryLauncher.launch(galleryIntent)
                    }
                    1 -> {
                        checkCameraPermissionAndLaunch()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun populateUI(pet: Pet) {
        etName.setText(pet.name)
        pet.birthDate?.let {
            birthDate = it
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            btnSetBirthDate.text = sdf.format(Date(it))
        }
        etSpecies.setText(pet.species ?: "")
        val genderPosition = genderOptions.indexOf(pet.gender)
        spinnerGender.setSelection(if (genderPosition != -1) genderPosition else 0)
        etMedicalHistory.setText(pet.medicalHistory ?: "")
        etNotes.setText(pet.notes ?: "")
        photoUri = pet.photoUri
        photoUri?.let {
            ivPetPhoto.setImageURI(Uri.parse(it))
        } ?: ivPetPhoto.setImageResource(R.drawable.ic_pet_placeholder)
    }

    private fun setupGenderSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGender.adapter = adapter
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        birthDate?.let { calendar.timeInMillis = it }
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            birthDate = calendar.timeInMillis
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            btnSetBirthDate.text = sdf.format(calendar.time)
        }
        DatePickerDialog(
            this, dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun checkCameraPermissionAndLaunch() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                launchCamera()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun launchCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(cameraIntent)
    }

    private fun saveBitmapAndGetUri(bitmap: Bitmap): Uri? {
        val filename = "pet_${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        var imageUri: Uri? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
            imageUri = Uri.fromFile(image)
        }

        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }

        return imageUri
    }

    private fun savePetDetails() {
        val name = etName.text.toString().trim()
        if (name.isEmpty()) {
            Toast.makeText(this, "Pet name is required", Toast.LENGTH_SHORT).show()
            return
        }
        val species = etSpecies.text.toString().trim()
        val gender = spinnerGender.selectedItem.toString()
        val medicalHistory = etMedicalHistory.text.toString().trim()
        val notes = etNotes.text.toString().trim()

        val updatedPet = currentPet?.copy(
            name = name,
            birthDate = birthDate,
            species = if (species.isEmpty()) null else species,
            gender = gender,
            medicalHistory = if (medicalHistory.isEmpty()) null else medicalHistory,
            notes = if (notes.isEmpty()) null else notes,
            photoUri = photoUri
        )
        updatedPet?.let {
            viewModel.updatePet(it)
            Toast.makeText(this, "Pet details updated!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
