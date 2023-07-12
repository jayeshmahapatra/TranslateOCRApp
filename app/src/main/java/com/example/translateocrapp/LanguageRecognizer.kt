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

    fun recognizeLanguage(ocrMap: Map<Rect, Text.Element>): String {

        // Create a string of all text elements
        val AllText = ocrMap.values.joinToString(separator = " ") { it.text }

        // Create a task to identify the language of the text
        val task: Task<String> = languageIdentifierClient.identifyLanguage(AllText)

        // Get the language code
        val languageCode: String = Tasks.await(task)

        // Return the language code
        return languageCode
    }
}