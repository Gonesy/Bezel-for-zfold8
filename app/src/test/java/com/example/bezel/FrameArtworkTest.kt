package com.example.bezel

import com.example.bezel.ui.ScreenMode
import com.example.bezel.ui.frameArtwork
import java.io.File
import javax.imageio.ImageIO
import org.junit.Assert.*
import org.junit.Test

class FrameArtworkTest {
    @Test fun allSixAssetsHaveMatchingDimensionsAndTransparentOpenings() {
        val resources = mutableSetOf<Int>()
        for (mode in ScreenMode.entries) for (finish in listOf("Graphite", "Lavender", "Cream")) {
            val artwork = frameArtwork(mode, finish)
            assertTrue(resources.add(artwork.resource))
            val prefix = if (mode == ScreenMode.OUTER) "cover" else "inner"
            val image = ImageIO.read(File("src/main/res/drawable-nodpi/${prefix}_${finish.lowercase()}.png"))
            assertEquals(artwork.width, image.width)
            assertEquals(artwork.height, image.height)
            assertTrue(image.colorModel.hasAlpha())
            fun alpha(x: Int, y: Int) = image.getRGB(x, y) ushr 24
            val cx = (artwork.screenLeft + artwork.screenRight) / 2
            val cy = (artwork.screenTop + artwork.screenBottom) / 2
            assertEquals(0, alpha(cx, cy))
            assertEquals(0, alpha(0, 0))
            // Screenshot fully covers the antialiased aperture without reaching the exterior.
            assertEquals(255, alpha(artwork.screenLeft - 1, cy))
            assertEquals(255, alpha(artwork.screenRight, cy))
            assertTrue(alpha(artwork.screenLeft + 3, cy) < 255)
            assertTrue(alpha(artwork.screenRight - 3, cy) < 255)
            val ratio = (artwork.screenRight - artwork.screenLeft).toFloat() /
                (artwork.screenBottom - artwork.screenTop)
            assertEquals(mode.displayAspectRatio, ratio, 0.001f)
        }
    }
}
