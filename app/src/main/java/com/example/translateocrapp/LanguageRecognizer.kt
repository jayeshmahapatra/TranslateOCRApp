package com.example.translateocrapp

import android.graphics.Rect
import android.util.Log
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentificationOptions
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.google.mlkit.vision.text.Text
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks


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

    fun recognizeLanguage(ocrMap: Map<Rect, Text.Line>): Map<Rect, String> {

        // Iterate through the map of OCR results and recognize the language of each line, store the results in a map

        // Create a map to store the language of each line
        val languageMap = mutableMapOf<Rect, String>()

        // Iterate through the map of OCR results
        for ((rect, line) in ocrMap) {
            // Get the text from the line
            val text = line.text

            // Create a task to recognize the language of the line
            val task: Task<String> = languageIdentifierClient.identifyLanguage(text)

            // Wait for the task to complete
            val result = Tasks.await(task)

            // Store the language of the line in the map
            languageMap[rect] = result
        }

        // Return the map of languages
        return languageMap

    }
}