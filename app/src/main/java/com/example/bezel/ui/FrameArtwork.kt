package com.example.bezel.ui

import androidx.annotation.DrawableRes
import com.example.bezel.R

/** Pixel coordinates include the antialiased boundary of the transparent display opening. */
internal data class FrameArtwork(
    @param:DrawableRes val resource: Int,
    val width: Int,
    val height: Int,
    val screenLeft: Int,
    val screenTop: Int,
    val screenRight: Int,
    val screenBottom: Int
) {
    val aspectRatio get() = width.toFloat() / height
}

internal fun frameArtwork(mode: ScreenMode, finish: String): FrameArtwork {
    val resource = when (mode) {
        ScreenMode.OUTER -> when (finish) {
            "Lavender" -> R.drawable.cover_lavender
            "Cream" -> R.drawable.cover_cream
            else -> R.drawable.cover_graphite
        }
        ScreenMode.INNER -> when (finish) {
            "Lavender" -> R.drawable.inner_lavender
            "Cream" -> R.drawable.inner_cream
            else -> R.drawable.inner_graphite
        }
    }
    return when (mode) {
        ScreenMode.OUTER -> FrameArtwork(resource, 1800, 2700, 161, 171, 1654, 2529)
        ScreenMode.INNER -> FrameArtwork(resource, 3360, 2600, 137, 135, 3223, 2465)
    }
}
