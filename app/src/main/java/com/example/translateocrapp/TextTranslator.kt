package com.example.translateocrapp

import android.graphics.Rect
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.text.Text

class TextTranslator {

    // Variables
    private lateinit var germanOptions : TranslatorOptions
    private lateinit var germanTranslator : com.google.mlkit.nl.translate.Translator

    private lateinit var swedishOptions : TranslatorOptions
    private lateinit var swedishTranslator : com.google.mlkit.nl.translate.Translator

    init {
        // Initialize the german translator
        germanOptions = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.GERMAN)
            .setTargetLanguage(TranslateLanguage.ENGLISH)
            .build()

        germanTranslator = com.google.mlkit.nl.translate.Translation.getClient(germanOptions)

        // Initialize the swedish translator
        swedishOptions = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.SWEDISH)
            .setTargetLanguage(TranslateLanguage.ENGLISH)
            .build()

        swedishTranslator = com.google.mlkit.nl.translate.Translation.getClient(swedishOptions)
    }


    // Create a function to translate text
    // sourceLanguageCode is the language code can have two values: "sv" or "de"
    fun translateTextToEnglish(text: String, sourceLanguageCode: String): String {

        // Check if the source language code is "sv" or "de", if not return the text
        if (sourceLanguageCode != "sv" && sourceLanguageCode != "de") {
            return text
        }

        // If the source language code is "sv" then translate the text to english using the swedish translator
        var task: Task<String> = if (sourceLanguageCode == "sv") {
            swedishTranslator.translate(text)

        }
        // If the source language code is "de" then translate the text to english using the german translator
        else {
            germanTranslator.translate(text)
        }

        return Tasks.await(task)

    }

    // Create a function to translate ocr result
    fun translateOcrResult(ocrResult: Map<Rect, Text.Element>, languageCode: String): Map<Rect, String> {
       return ocrResult.mapValues { translateTextToEnglish(it.value.text, languageCode) }
    }
}