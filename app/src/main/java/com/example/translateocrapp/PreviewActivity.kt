package com.example.translateocrapp

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView



class PreviewActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_IMAGE_PATH = "extra_image_path"
    }

    private lateinit var previewImageView: ImageView
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        previewImageView = findViewById(R.id.previewImageView)
        backButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        val imagePath = intent.getStringExtra(EXTRA_IMAGE_PATH)
        if (imagePath != null) {
            displayImage(imagePath)
        } else {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun displayImage(imagePath: String) {
        val bitmap = BitmapFactory.decodeFile(imagePath)
        val rotatedBitmap = rotateBitmap(imagePath, bitmap)
        previewImageView.setImageBitmap(rotatedBitmap)
    }

    private fun rotateBitmap(imagePath: String, bitmap: Bitmap): Bitmap {
        val exif = ExifInterface(imagePath)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )

        val rotationDegrees = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }

        return if (rotationDegrees != 0) {
            val matrix = android.graphics.Matrix()
            matrix.postRotate(rotationDegrees.toFloat())
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } else {
            bitmap
        }
    }
}