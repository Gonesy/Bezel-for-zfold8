package com.example.bezel

import com.example.bezel.ui.ScreenMode
import com.example.bezel.ui.frameArtwork
import com.example.bezel.util.cropSize
import org.junit.Assert.*
import org.junit.Test

class ExportGeometryTest {
    @Test fun outputUsesFullArtworkResolutionForEveryFinish() {
        for (finish in listOf("Graphite", "Cream", "Lavender")) {
            val cover = frameArtwork(ScreenMode.OUTER, finish)
            val inner = frameArtwork(ScreenMode.INNER, finish)
            assertEquals(1800, cover.width)
            assertEquals(2700, cover.height)
            assertEquals(3360, inner.width)
            assertEquals(2600, inner.height)
        }
    }
    @Test fun centeredCropCoversOpeningWithoutStretchingPortraitOrLandscapeSources() {
        for ((sw, sh) in listOf(1248 to 1972, 2448 to 1848, 4000 to 1000, 800 to 2400)) {
            for (mode in ScreenMode.entries) {
                val frame = frameArtwork(mode, "Graphite")
                val tw = frame.screenRight - frame.screenLeft
                val th = frame.screenBottom - frame.screenTop
                val (w, h) = cropSize(sw, sh, tw, th)
                assertTrue(w >= tw - 0.001f && h >= th - 0.001f)
                assertEquals(sw.toFloat() / sh, w / h, 0.00001f)
                assertTrue(kotlin.math.abs(w - tw) < 0.001f || kotlin.math.abs(h - th) < 0.001f)
            }
        }
    }
}
