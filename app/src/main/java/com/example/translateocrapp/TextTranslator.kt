package com.example.translateocrapp


import android.app.Activity
import android.graphics.Rect
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks

import android.content.Context
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import android.os.Handler
import android.os.Looper

import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.text.Text

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateRemoteModel

class TextTranslator(private val context: Context) {

    // Variables
    private lateinit var germanOptions : TranslatorOptions
    private lateinit var germanTranslator : com.google.mlkit.nl.translate.Translator

    private lateinit var swedishOptions : TranslatorOptions
    private lateinit var swedishTranslator : com.google.mlkit.nl.translate.Translator

    private val remoteModelManager = RemoteModelManager.getInstance()

    // AlertDialog to show download progress
    private var progressDialog: AlertDialog? = null


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
    private fun translateTextToEnglish(text: String, sourceLanguageCode: String): String {

        // Check if the source language code is "sv" or "de", if not return the text
        if (sourceLanguageCode != "sv" && sourceLanguageCode != "de") {
            return text
        }

        // Check if the translation model is downloaded and available
        if (!isModelDownloaded(sourceLanguageCode)) {
            // Model not downloaded, download it and wait for completion
            downloadModel(sourceLanguageCode)
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



    // Check if the translation model for the given language code is downloaded and available
    private fun isModelDownloaded(languageCode: String): Boolean {

        val model = TranslateRemoteModel.Builder(languageCode).build()
        val task = remoteModelManager.isModelDownloaded(model)
        return Tasks.await(task)

    }

    // Download the translation model for the given language code
    private fun downloadModel(languageCode: String) {

        // Create a progress dialog
        val progressBar = ProgressBar(context).apply {
            isIndeterminate = true
        }

        Handler(Looper.getMainLooper()).post {
            progressDialog = AlertDialog.Builder(context)
                .setTitle("Downloading Translation Model (Please Be on Wifi)")
                .setCancelable(false)
                .setView(progressBar)
                .show()
        }



        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        val model = TranslateRemoteModel.Builder(languageCode).build()
        val downloadTask = remoteModelManager.download(model, conditions)

        try {
            Tasks.await(downloadTask)
            Handler(Looper.getMainLooper()).post {
                progressDialog?.dismiss()
                progressDialog = null
            }

            // Show a toast indicating successful download
            showDownloadToast("Translation Model Downloaded Successfully")

        } catch (e: Exception) {

            // Dismiss the progress dialog on download failure
            Handler(Looper.getMainLooper()).post {
                progressDialog?.dismiss()
                progressDialog = null
            }

            // Show a toast indicating download failure
            showDownloadToast("Translation Model Download Failed")

        }
    }

    // Show toast on the main UI thread
    private fun showDownloadToast(message: String) {
        (context as? Activity)?.runOnUiThread {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    // Create a function to translate ocr result
    fun translateOcrResult(ocrResult: Map<Rect, Text.TextBlock>, languageCode: String ): Map<Rect, String> {

        // Create a map to store the translated result
        val translatedResult = mutableMapOf<Rect, String>()

        // Iterate through the ocr result
        for ((rect, textBlock) in ocrResult) {

            // Translate the textBlock text to english
            val translatedText = translateTextToEnglish(textBlock.text, languageCode)

            // Add the translated text to the map
            translatedResult[rect] = translatedText
        }

        return translatedResult

    }
}