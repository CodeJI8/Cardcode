package com.singleminds.cardcode

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class CardTheme(val bg: Int, val accent: Int, val text: Int, val qrBg: Int, val qrFg: Int) : Parcelable {
    Ink(0xFF14110F.toInt(), 0xFFF2A93B.toInt(), 0xFFF4EBDD.toInt(), 0xFFF4EBDD.toInt(), 0xFF14110F.toInt()),
    Paper(0xFFF4EBDD.toInt(), 0xFFD46A43.toInt(), 0xFF14110F.toInt(), 0xFFFFFFFF.toInt(), 0xFF14110F.toInt()),
    Forest(0xFF1B3B36.toInt(), 0xFFF4EBDD.toInt(), 0xFFF4EBDD.toInt(), 0xFFF4EBDD.toInt(), 0xFF1B3B36.toInt()),
    Midnight(0xFF0F172A.toInt(), 0xFFE0F2FE.toInt(), 0xFFF8FAFC.toInt(), 0xFFF8FAFC.toInt(), 0xFF0F172A.toInt())
}

object CardRenderer {
    fun render(
        theme: CardTheme,
        payload: String,
        headline: String,
        subtitle: String
    ): Bitmap {
        val width = 1080
        val height = 1528
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.drawColor(theme.bg)

        val textPaint = android.text.TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = theme.text
        }

        var currentTextSize = 80f
        var layout: android.text.StaticLayout
        val maxTextWidth = width - 160
        do {
            textPaint.textSize = currentTextSize
            layout = android.text.StaticLayout.Builder.obtain(headline, 0, headline.length, textPaint, maxTextWidth)
                .setAlignment(android.text.Layout.Alignment.ALIGN_CENTER)
                .setLineSpacing(0f, 1.2f)
                .setMaxLines(3)
                .setEllipsize(android.text.TextUtils.TruncateAt.END)
                .build()
            currentTextSize -= 4f
        } while (layout.height > 250 && currentTextSize > 40f)

        canvas.save()
        canvas.translate(80f, 150f + (250f - layout.height) / 2f)
        layout.draw(canvas)
        canvas.restore()

        val qrSize = 600
        val qrTop = 450f
        val qrRect = RectF(
            (width - qrSize) / 2f - 40f,
            qrTop - 40f,
            (width + qrSize) / 2f + 40f,
            qrTop + qrSize + 40f
        )
        val panelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = theme.qrBg }
        canvas.drawRoundRect(qrRect, 60f, 60f, panelPaint)

        if (payload.isNotEmpty()) {
            val hints = mapOf(
                EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H,
                EncodeHintType.MARGIN to 0
            )
            val writer = QRCodeWriter()
            try {
                val bitMatrix = writer.encode(payload, BarcodeFormat.QR_CODE, qrSize, qrSize, hints)
                val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = theme.qrFg }
                val moduleSize = qrSize.toFloat() / bitMatrix.width

                val finders = listOf(
                    Pair(0, 0),
                    Pair(bitMatrix.width - 7, 0),
                    Pair(0, bitMatrix.height - 7)
                )
                
                for (finder in finders) {
                    val fx = finder.first
                    val fy = finder.second
                    val left = (width - qrSize) / 2f + fx * moduleSize
                    val top = qrTop + fy * moduleSize
                    val size = 7 * moduleSize
                    
                    val outerRect = RectF(left, top, left + size, top + size)
                    canvas.drawRoundRect(outerRect, moduleSize * 1.5f, moduleSize * 1.5f, dotPaint)
                    
                    val innerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = theme.qrBg }
                    val innerRect = RectF(left + moduleSize, top + moduleSize, left + size - moduleSize, top + size - moduleSize)
                    canvas.drawRoundRect(innerRect, moduleSize * 0.8f, moduleSize * 0.8f, innerPaint)
                    
                    val centerRect = RectF(left + 2 * moduleSize, top + 2 * moduleSize, left + size - 2 * moduleSize, top + size - 2 * moduleSize)
                    canvas.drawRoundRect(centerRect, moduleSize * 0.5f, moduleSize * 0.5f, dotPaint)
                }

                for (x in 0 until bitMatrix.width) {
                    for (y2 in 0 until bitMatrix.height) {
                        val isFinder = (x < 7 && y2 < 7) || (x > bitMatrix.width - 8 && y2 < 7) || (x < 7 && y2 > bitMatrix.height - 8)
                        if (!isFinder && bitMatrix.get(x, y2)) {
                            val left = (width - qrSize) / 2f + x * moduleSize
                            val top = qrTop + y2 * moduleSize
                            canvas.drawCircle(left + moduleSize / 2f, top + moduleSize / 2f, moduleSize / 2f * 0.9f, dotPaint)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        var subTextSize = 50f
        var subLayout: android.text.StaticLayout
        do {
            textPaint.textSize = subTextSize
            subLayout = android.text.StaticLayout.Builder.obtain(subtitle, 0, subtitle.length, textPaint, maxTextWidth)
                .setAlignment(android.text.Layout.Alignment.ALIGN_CENTER)
                .setLineSpacing(0f, 1.2f)
                .setMaxLines(2)
                .setEllipsize(android.text.TextUtils.TruncateAt.END)
                .build()
            subTextSize -= 4f
        } while (subLayout.height > 150 && subTextSize > 30f)

        canvas.save()
        canvas.translate(80f, qrTop + qrSize + 90f)
        subLayout.draw(canvas)
        canvas.restore()

        textPaint.textSize = 36f
        textPaint.alpha = 150
        canvas.drawText("Point your camera at the code", width / 2f, height - 100f, textPaint)

        return bitmap
    }
}
