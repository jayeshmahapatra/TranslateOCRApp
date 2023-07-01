package com.example.translateocrapp

import android.graphics.Bitmap
import android.graphics.Rect
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.TextRecognizerOptionsInterface
import com.google.mlkit.vision.text.latin.TextRecognizerOptions


class OcrHelper {

    private val textRecognizer: TextRecognizer
    private val textRecognizerOptions: TextRecognizerOptionsInterface

    init {
        textRecognizerOptions = TextRecognizerOptions.Builder().build()
        textRecognizer = TextRecognition.getClient(textRecognizerOptions)
    }


    fun performOcr(bitmap: Bitmap): Map<Rect, Text.TextBlock> {
        val image = InputImage.fromBitmap(bitmap, 0)
        val task: Task<Text> = textRecognizer.process(image)
        val result = Tasks.await(task)
        return extractTextBlocks(result)
    }

    private fun extractTextBlocks(text: Text): Map<Rect, Text.TextBlock> {
        val textData = mutableMapOf<Rect, Text.TextBlock>()

        for (textBlock in text.textBlocks) {
            val boundingBox = textBlock.boundingBox

            if (boundingBox != null) {
                textData[boundingBox] = textBlock
            }
        }

        return textData
    }


}