package com.example.bezel.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import com.example.bezel.ui.FrameArtwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max

/** Same centered crop as the editor, expressed in output pixels. */
internal fun cropSize(sourceWidth: Int, sourceHeight: Int, targetWidth: Int, targetHeight: Int): Pair<Float, Float> {
    require(sourceWidth > 0 && sourceHeight > 0 && targetWidth > 0 && targetHeight > 0)
    val scale = max(targetWidth.toFloat() / sourceWidth, targetHeight.toFloat() / sourceHeight)
    return sourceWidth * scale to sourceHeight * scale
}

internal object MockupRenderer {
    suspend fun render(context: Context, uri: String, artwork: FrameArtwork, background: Int?): Bitmap =
        withContext(Dispatchers.Default) {
            val targetWidth = artwork.screenRight - artwork.screenLeft
            val targetHeight = artwork.screenBottom - artwork.screenTop
            // ImageDecoder applies image orientation; software bitmaps can be drawn offscreen.
            val source = ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, Uri.parse(uri))) { decoder, info, _ ->
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                val scale = max(targetWidth.toFloat() / info.size.width, targetHeight.toFloat() / info.size.height).coerceAtMost(1f)
                decoder.setTargetSize(max(1, (info.size.width * scale).toInt()), max(1, (info.size.height * scale).toInt()))
            }
            try {
                val frame = requireNotNull(BitmapFactory.decodeResource(context.resources, artwork.resource,
                    BitmapFactory.Options().apply { inScaled = false }))
                try {
                    check(frame.width == artwork.width && frame.height == artwork.height)
                    val result = Bitmap.createBitmap(artwork.width, artwork.height, Bitmap.Config.ARGB_8888)
                    try {
                        val canvas = Canvas(result)
                        background?.let { canvas.drawColor(it) }
                        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
                        val (width, height) = cropSize(source.width, source.height, targetWidth, targetHeight)
                        val left = artwork.screenLeft + (targetWidth - width) / 2f
                        val top = artwork.screenTop + (targetHeight - height) / 2f
                        canvas.save()
                        canvas.clipRect(artwork.screenLeft, artwork.screenTop, artwork.screenRight, artwork.screenBottom)
                        canvas.drawBitmap(source, null, RectF(left, top, left + width, top + height), paint)
                        canvas.restore()
                        canvas.drawBitmap(frame, 0f, 0f, paint)
                        result
                    } catch (error: Throwable) {
                        result.recycle()
                        throw error
                    }
                } finally { frame.recycle() }
            } finally { source.recycle() }
        }
}
