package com.example.translateocrapp

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.media.ExifInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView

import com.google.mlkit.vision.text.Text

// Import coroutines dependencies
import kotlinx.coroutines.Job
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import com.example.translateocrapp.OcrHelper
import com.example.translateocrapp.LanguageRecognizer


class PreviewActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_IMAGE_PATH = "extra_image_path"
    }

    private lateinit var previewImageView: ImageView
    private lateinit var backButton: Button

    // Create an instance of the OcrHelper class
    private val ocrHelper = OcrHelper()
    private lateinit var ocrResult: Map<Rect, Text.Element>

    // Create an instance of the LanguageRecognizer class
    private val languageRecognizer = LanguageRecognizer()

    // Job variable to keep track of the OCR job
    private lateinit var ocrJob: Job

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

            // Get the bitmap from the image file
            val bitmap = readImageFile(imagePath)

            // Display the bitmap
            displayBitmap(bitmap)

            // Create a thread to run the OCR
            // Perform OCR in a background thread
            ocrJob = CoroutineScope(Dispatchers.Default).launch {
                ocrResult = ocrHelper.performOcr(bitmap)

                withContext(Dispatchers.Main) {
                    // Handle the OCR result here
                    processOcrResult(ocrResult)
                }
            }

            // Wait for the OCR job to complete before starting the language identification job
            ocrJob.invokeOnCompletion {
                // Perform language identification in a separate background thread
                CoroutineScope(Dispatchers.Default).launch {
                    val languageResult = recognizeLanguageInBackground(ocrResult)

                    withContext(Dispatchers.Main) {
                        // Handle the language identification result here
                        processLanguageResult(languageResult)
                    }
                }
            }


        } else {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    // Create a function to process the OCR result
    private fun processOcrResult(ocrResult: Map<Rect, Text.Element>) {
        // Log the OCR result with Rect and text
        for ((rect, textElement) in ocrResult) {
            Log.d("OCR", "Found text ${textElement.text} at $rect")
        }

    }

    // Create a function to process the language identification result
    private fun processLanguageResult(languageResult: String) {
        // Handle the language identification result
        Log.d("Language Identification", "Detected language: $languageResult")

    }

    // Create a function to read image file and return bitmap
    private fun readImageFile(imagePath: String): Bitmap {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        val bitmap = BitmapFactory.decodeFile(imagePath, options)
        // Rotate the bitmap if required and return it
        return rotateBitmap(imagePath, bitmap)
    }

    // Create a function to display the bitmap
    private fun displayBitmap(bitmap: Bitmap) {
        previewImageView.setImageBitmap(bitmap)
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

    private suspend fun recognizeLanguageInBackground(ocrResult: Map<Rect, Text.Element>): String {
        return withContext(Dispatchers.Default) {
            // Perform language identification using LanguageRecognizer in a background thread
            languageRecognizer.recognizeLanguage(ocrResult)
        }
    }

}