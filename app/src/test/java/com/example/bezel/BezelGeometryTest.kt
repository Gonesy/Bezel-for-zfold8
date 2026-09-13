package com.example.bezel

import com.example.bezel.ui.ScreenMode
import com.example.bezel.ui.detectScreenMode
import org.junit.Assert.assertEquals
import org.junit.Test

class BezelGeometryTest {
    @Test
    fun outerCanvasMatchesFoldedDeviceDimensions() {
        assertEquals(81.9f / 123.9f, ScreenMode.OUTER.aspectRatio, 0.0001f)
        assertEquals(1248f / 1972f, ScreenMode.OUTER.displayAspectRatio, 0.0001f)
    }

    @Test
    fun innerCanvasMatchesUnfoldedDeviceDimensions() {
        assertEquals(161.4f / 123.9f, ScreenMode.INNER.aspectRatio, 0.0001f)
        assertEquals(2448f / 1848f, ScreenMode.INNER.displayAspectRatio, 0.0001f)
    }

    @Test
    fun screenshotAspectRatioSelectsMatchingDisplay() {
        assertEquals(ScreenMode.OUTER, detectScreenMode(width = 1248, height = 1972))
        assertEquals(ScreenMode.INNER, detectScreenMode(width = 2448, height = 1848))
        assertEquals(ScreenMode.OUTER, detectScreenMode(width = 1080, height = 2400))
        assertEquals(ScreenMode.INNER, detectScreenMode(width = 2208, height = 1840))
    }

    @Test
    fun screenSelectorIconsUsePublishedDisplayRatios() {
        assertEquals(1248f / 1972f, ScreenMode.OUTER.iconAspectRatio, 0.0001f)
        assertEquals(2448f / 1848f, ScreenMode.INNER.iconAspectRatio, 0.0001f)
        assertEquals(0.5f, ScreenMode.INNER.cameraHorizontalBias, 0.0001f)
    }

    @Test
    fun invalidImageDimensionsFallBackToOuterScreen() {
        assertEquals(ScreenMode.OUTER, detectScreenMode(width = 0, height = 0))
    }
}
