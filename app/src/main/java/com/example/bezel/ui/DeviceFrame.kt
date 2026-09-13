package com.example.bezel.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage

@Composable
fun MockupCanvas(uri: String, screenMode: ScreenMode, bezelOption: BezelOption, modifier: Modifier = Modifier) {
    val artwork = frameArtwork(screenMode, bezelOption.name)
    BoxWithConstraints(modifier.fillMaxHeight(0.9f).aspectRatio(artwork.aspectRatio)) {
        val pixel = maxWidth / artwork.width
        // The PNG itself masks corners and contains the lens. Do not draw another bezel
        // or camera over it. Keep source pixels and all material effects unmodified.
        AsyncImage(
            model = uri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .offset(pixel * artwork.screenLeft, pixel * artwork.screenTop)
                .size(pixel * (artwork.screenRight - artwork.screenLeft),
                    pixel * (artwork.screenBottom - artwork.screenTop))
        )
        Image(
            painter = painterResource(artwork.resource),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )
    }
}
