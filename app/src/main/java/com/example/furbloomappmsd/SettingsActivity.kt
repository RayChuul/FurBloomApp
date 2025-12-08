package com.example.furbloomappmsd

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit

class SettingsActivity : AppCompatActivity() {

    private lateinit var ivPetPhoto: ImageView
    private lateinit var btnTakePhoto: Button
    private lateinit var btnClearData: Button
    private lateinit var takePhotoLauncher: ActivityResultLauncher<Intent>

    private val petPhotoKey = "petPhotoUri"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        ivPetPhoto = findViewById(R.id.iv_pet_photo)
        btnTakePhoto = findViewById(R.id.btn_take_photo)
        btnClearData = findViewById(R.id.btn_clear_data)

        val sharedPref: SharedPreferences = getSharedPreferences("pet_prefs", Context.MODE_PRIVATE)

        @Suppress("DEPRECATION")
        val uriString = sharedPref.getString(petPhotoKey, null)
        if (!uriString.isNullOrEmpty()) {
            val uri = Uri.parse(uriString)
            ivPetPhoto.setImageURI(uri)
        }

        // Camera launcher
        takePhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val bitmap = result.data?.extras?.get("data") as? Bitmap
                    bitmap?.let {
                        ivPetPhoto.setImageBitmap(it)
                        // Save bitmap and store URI
                        val savedUri = saveBitmapToGallery(it)
                        savedUri?.let { uri ->
                            sharedPref.edit {
                                putString(petPhotoKey, uri.toString())
                            }
                        }
                    }
                }
            }

        btnTakePhoto.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePhotoLauncher.launch(intent)
        }

        btnClearData.setOnClickListener {
            sharedPref.edit {
                remove(petPhotoKey)
            }
            ivPetPhoto.setImageResource(R.drawable.ic_pet_placeholder)
        }
    }

    private fun saveBitmapToGallery(bitmap: Bitmap): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "PetPhoto_${System.currentTimeMillis()}.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyPetsApp")
            }
        }

        val resolver = contentResolver
        val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        imageUri?.let { uri ->
            resolver.openOutputStream(uri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
        }

        return imageUri
    }
}
