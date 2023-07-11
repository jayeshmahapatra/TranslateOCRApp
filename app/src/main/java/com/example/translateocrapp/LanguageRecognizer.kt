package com.example.translateocrapp

import android.graphics.Rect
import android.util.Log
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentificationOptions
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.google.mlkit.vision.text.Text

class LanguageRecognizer {

    private val languageIdentifierClient: LanguageIdentifier
    private val languageIdentifierOptions: LanguageIdentificationOptions


    init {
        // Initialize the language identifier client in the class constructor
        languageIdentifierOptions = LanguageIdentificationOptions.Builder()
            .setConfidenceThreshold(0.5f)
            .build()
        languageIdentifierClient = LanguageIdentification.getClient(languageIdentifierOptions)
    }

    fun recognizeLanguage(ocrMap: Map<Rect, Text.Element>): String {
        val languageIds = mutableMapOf<String, Int>()

        // Go through each element in the map and predict the language
        for (element in ocrMap.values) {
            val elementText = element.text

            // Identify the language of the element text
            languageIdentifierClient.identifyLanguage(elementText)
                .addOnSuccessListener { languageCode ->
                    val count = languageIds.getOrDefault(languageCode, 0) + 1
                    languageIds[languageCode] = count
                }
                .addOnFailureListener { exception ->
                    // Handle the language identification failure

                    // Log the exception
                    Log.e("LanguageRecognizer","Exception thrown while identifying language: $exception")
                }
        }

        // Return the most common language across all elements
        return languageIds.maxByOrNull { it.value }?.key ?: ""
    }
}