package com.example.translateocrapp

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.text.TextPaint
import com.google.mlkit.vision.text.Text

object BitmapAnnotator {

    // Function to take as input a bitmap, a map of OCR results as well as their translations, and return a bitmap annotated with translated ocr results
    public fun annotateBitmap(bitmap: Bitmap, ocrResult: Map<Rect, Text.Line>, translatedOcrResult: Map<Rect, String>): Bitmap {

        // Create a mutable copy of the bitmap
        val annotatedBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        // Get a canvas to draw on the bitmap
        val canvas = Canvas(annotatedBitmap)

        // Get paint objects to draw on the bitmap
        val rectPaint = Paint()
        val textPaint = TextPaint()

        // Iterate over ocrResult and translatedOcrResult and draw the translated text on the bitmap
        for ((rect, line) in ocrResult) {
            // Get the translated text from the translatedOcrResult map
            val translatedText = translatedOcrResult[rect]

            // Draw a filled rectangle on the bitmap in rect coordinates

            val clampedRect = rect.clampToBitmap(bitmap) // Helper function to clamp the rect to the bitmap
            val rectF = RectF(clampedRect)
            rectPaint.color = getAverageColor(bitmap, clampedRect)
            canvas.drawRect(rectF, rectPaint)


            // Draw the translated text on the bitmap in rect coordinates
            // Adjust the text size to fit the rectangle
            // Draw the translated text on the bitmap in rect coordinates
            textPaint.color = getContrastingColor(rectPaint.color)
            textPaint.typeface = Typeface.DEFAULT_BOLD
            textPaint.textSize = getTextSizeToFitRect(rectF, translatedText ?: "")
            canvas.drawText(translatedText ?: "", rectF.left, rectF.bottom, textPaint)


        }

        return annotatedBitmap
    }

    private fun Rect.clampToBitmap(bitmap: Bitmap): Rect {
        val clampedLeft = left.coerceIn(0, bitmap.width)
        val clampedTop = top.coerceIn(0, bitmap.height)
        val clampedRight = right.coerceIn(0, bitmap.width)
        val clampedBottom = bottom.coerceIn(0, bitmap.height)
        return Rect(clampedLeft, clampedTop, clampedRight, clampedBottom)
    }

    // Function to get the average color of a region in a bitmap
    private fun getAverageColor(bitmap: Bitmap, rect: Rect): Int {
        var redSum = 0
        var greenSum = 0
        var blueSum = 0
        var pixelCount = 0

        for (x in rect.left until rect.right) {
            for (y in rect.top until rect.bottom) {
                val pixel = bitmap.getPixel(x, y)
                redSum += Color.red(pixel)
                greenSum += Color.green(pixel)
                blueSum += Color.blue(pixel)
                pixelCount++
            }
        }

        val averageRed = redSum / pixelCount
        val averageGreen = greenSum / pixelCount
        val averageBlue = blueSum / pixelCount

        return Color.rgb(averageRed, averageGreen, averageBlue)
    }

    // Helper function to get a contrasting color for text based on the background color
    private fun getContrastingColor(backgroundColor: Int): Int {
        val luminance = (0.299 * Color.red(backgroundColor) +
                0.587 * Color.green(backgroundColor) +
                0.114 * Color.blue(backgroundColor)) / 255
        return if (luminance > 0.5) Color.BLACK else Color.WHITE
    }

    // Helper function to calculate the text size that fits within the given rectangle
    private fun getTextSizeToFitRect(rect: RectF, text: String): Float {
        val textPaint = Paint()
        textPaint.textSize = 100f
        var textSize = textPaint.textSize

        while (textPaint.measureText(text) > rect.width() && textSize > 0) {
            textSize -= 1f
            textPaint.textSize = textSize
        }

        return textSize
    }
}