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

    fun recognizeLanguage(ocrMap: Map<Rect, Text.TextBlock>): String {

        // Iterate through the map of OCR results and recognize the language of each textBlock
        // Find the most common language that is either German or Swedish
        // if neither German nor Swedish is found, return "und"

        // Create a map to store the language of each line
        val languageMap = mutableMapOf<Rect, String>()

        // Iterate through the map of OCR results
        for ((rect, textBlock) in ocrMap) {
            // Get the text from the textBlock
            val text = textBlock.text

            // Create a task to recognize the language of the textBlock
            val task: Task<String> = languageIdentifierClient.identifyLanguage(text)

            // Wait for the task to complete
            val result = Tasks.await(task)

            // Store the language of the textBlock in the map
            languageMap[rect] = result
        }

        // Count the occurrences of German and Swedish languages
        val germanCount = languageMap.values.count { it == "de" }
        val swedishCount = languageMap.values.count { it == "sv" }

        return when {
            germanCount > 0 && swedishCount > 0 -> {
                // Both German and Swedish are present, return the most common between them
                if (germanCount >= swedishCount) "de" else "sv"
            }
            germanCount > 0 -> "de" // Only German is present
            swedishCount > 0 -> "sv" // Only Swedish is present
            else -> "und" // Neither German nor Swedish is found
        }


    }
}