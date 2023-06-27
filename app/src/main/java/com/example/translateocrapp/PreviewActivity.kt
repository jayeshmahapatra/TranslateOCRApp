package com.example.translateocrapp

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView



class PreviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        // Retrieve the captured image bitmap from the intent
        val bitmap: Bitmap? = intent.getParcelableExtra("capturedImage")

        // Display the captured image in the ImageView
        val imageView: ImageView = findViewById(R.id.previewImageView)
        imageView.setImageBitmap(bitmap)

        // Add a click listener to the back button
        val backButton: Button = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            // Finish the activity and return to the main activity
            finish()

        }
    }
}