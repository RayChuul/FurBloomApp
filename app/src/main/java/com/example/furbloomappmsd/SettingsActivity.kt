package com.example.furbloomappmsd

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        ivPetPhoto = findViewById(R.id.iv_pet_photo)
        btnTakePhoto = findViewById(R.id.btn_take_photo)
        btnClearData = findViewById(R.id.btn_clear_data)

        // Initialize SharedPreferences
        val sharedPref = getSharedPreferences("pet_prefs", Context.MODE_PRIVATE)

        // Load saved pet photo (optional: could save Uri or Bitmap)
        val savedPhoto = sharedPref.getString("petPhotoUri", null)
        // TODO: Load savedPhoto into ivPetPhoto if available

        // Activity Result API for taking photo
        takePhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as? Bitmap
                imageBitmap?.let {
                    ivPetPhoto.setImageBitmap(it)
                    // Save the bitmap Uri or handle saving to storage
                    // Here, just a placeholder for SharedPreferences
                    sharedPref.edit {
                        putBoolean("photo_set", true)
                    }
                }
            }
        }

        btnTakePhoto.setOnClickListener {
            val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            takePhotoLauncher.launch(intent)
        }

        btnClearData.setOnClickListener {
            sharedPref.edit {
                clear()
            }
            ivPetPhoto.setImageResource(R.drawable.ic_pet) // default placeholder
        }
    }
}
