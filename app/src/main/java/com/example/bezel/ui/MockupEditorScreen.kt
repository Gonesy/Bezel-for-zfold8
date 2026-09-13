package com.example.bezel.ui

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bezel.ui.theme.BezelTheme
import androidx.compose.ui.graphics.toArgb
import com.example.bezel.util.MockupRenderer
import kotlinx.coroutines.CancellationException
import com.example.bezel.util.ExportUtils
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs

enum class ScreenMode(
    val aspectRatio: Float,
    val displayAspectRatio: Float,
    val iconAspectRatio: Float,
    val cameraHorizontalBias: Float
) {
    // Published ratios drive automatic mode detection and selector icons.
    // FrameArtwork holds the separate pixel geometry of the supplied PNG overlays.
    OUTER(
        aspectRatio = 81.9f / 123.9f,
        displayAspectRatio = 1248f / 1972f,
        iconAspectRatio = 1248f / 1972f,
        cameraHorizontalBias = 0f
    ),
    INNER(
        aspectRatio = 161.4f / 123.9f,
        displayAspectRatio = 2448f / 1848f,
        iconAspectRatio = 2448f / 1848f,
        cameraHorizontalBias = 0.5f
    )
}

internal fun detectScreenMode(width: Int, height: Int): ScreenMode {
    if (width <= 0 || height <= 0) return ScreenMode.OUTER
    val ratio = width.toFloat() / height
    return ScreenMode.entries.minBy { abs(ratio - it.displayAspectRatio) }
}

data class BezelOption(
    val name: String,
    val previewColor: Color,
    val colors: List<Color>
)

val BezelOptions = listOf(
    BezelOption(
        name = "Lavender",
        previewColor = Color(0xFFBAB7C7),
        colors = listOf(
            Color(0xFFE3E1E8),
            Color(0xFFB9B6C6),
            Color(0xFF858290),
            Color(0xFFC9C6D2),
            Color(0xFFF0EEF3)
        )
    ),
    BezelOption(
        name = "Graphite",
        previewColor = Color(0xFF666A6E),
        colors = listOf(
            Color(0xFF666A6D),
            Color(0xFF3D4145),
            Color(0xFF202326),
            Color(0xFF484C50),
            Color(0xFF73777A)
        )
    ),
    BezelOption(
        name = "Cream",
        previewColor = Color(0xFFEAE9E6),
        colors = listOf(
            Color(0xFFFFFFFF),
            Color(0xFFE9E8E4),
            Color(0xFFC4C2BC),
            Color(0xFFF2F0EB),
            Color(0xFFFFFFFF)
        )
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MockupEditorScreen(
    uri: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var screenMode by remember { mutableStateOf(ScreenMode.OUTER) }
    var screenModeWasManuallySelected by remember { mutableStateOf(false) }
    var selectedBezelOption by remember { mutableStateOf(BezelOptions[1]) }
    var backgroundColor by remember { mutableStateOf(Color(0xFFF0F0F0)) }
    var showBackground by remember { mutableStateOf(false) }
    var isFullscreen by remember { mutableStateOf(false) }
    var zoom by remember(screenMode) { mutableFloatStateOf(1f) }
    var panX by remember(screenMode) { mutableFloatStateOf(0f) }
    var panY by remember(screenMode) { mutableFloatStateOf(0f) }
    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        val nextZoom = (zoom * zoomChange).coerceIn(1f, 5f)
        if (nextZoom == 1f) {
            panX = 0f
            panY = 0f
        } else {
            panX += panChange.x
            panY += panChange.y
        }
        zoom = nextZoom
    }
    val previewInteractionSource = remember { MutableInteractionSource() }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var isSaving by remember { mutableStateOf(false) }

    LaunchedEffect(uri) {
        val detectedMode = withContext(Dispatchers.IO) {
            runCatching {
                context.contentResolver.openInputStream(Uri.parse(uri))?.use { stream ->
                    val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    BitmapFactory.decodeStream(stream, null, options)
                    detectScreenMode(options.outWidth, options.outHeight)
                }
            }.getOrNull()
        }
        if (!screenModeWasManuallySelected && detectedMode != null) {
            screenMode = detectedMode
        }
    }

    Scaffold(
        topBar = {
            if (!isFullscreen) TopAppBar(
                title = { Text("Mockup Editor") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(enabled = !isSaving, onClick = {
                        val artwork = frameArtwork(screenMode, selectedBezelOption.name)
                        val exportBackground = if (showBackground) backgroundColor.toArgb() else null
                        isSaving = true
                        coroutineScope.launch {
                            try {
                                val bitmap = MockupRenderer.render(context, uri, artwork, exportBackground)
                                val savedUri = try {
                                    ExportUtils.saveBitmapToGallery(context, bitmap)
                                } finally { bitmap.recycle() }
                                if (savedUri != null) {
                                    snackbarHostState.showSnackbar("Mockup saved to gallery")
                                } else {
                                    snackbarHostState.showSnackbar("Failed to save mockup")
                                }
                            } catch (e: CancellationException) {
                                throw e
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Error: ${e.message}")
                            } finally {
                                isSaving = false
                            }
                        }
                    }) {
                        Text(if (isSaving) "Saving…" else "Save")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(if (isFullscreen) Modifier else Modifier.padding(bottom = 88.dp))
                    .background(if (showBackground) backgroundColor else Color.Transparent)
                    .clickable(
                        interactionSource = previewInteractionSource,
                        indication = null,
                        onClick = { isFullscreen = !isFullscreen }
                    )
                    .testTag("mockup_preview"),
                contentAlignment = Alignment.Center
            ) {
                MockupCanvas(
                    uri = uri,
                    screenMode = screenMode,
                    bezelOption = selectedBezelOption,
                    modifier = Modifier
                        .padding(if (isFullscreen) 8.dp else 16.dp)
                        .graphicsLayer {
                            scaleX = zoom
                            scaleY = zoom
                            translationX = panX
                            translationY = panY
                        }
                        .transformable(transformState)
                )
            }

            if (!isFullscreen) {
                if (showBackground) {
                    BackgroundPalette(
                        selectedColor = backgroundColor,
                        onColorSelected = { backgroundColor = it },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 12.dp, bottom = 96.dp)
                    )
                }
                EditorControls(
                    screenMode = screenMode,
                    onScreenModeChange = {
                        screenModeWasManuallySelected = true
                        screenMode = it
                    },
                    selectedBezelOption = selectedBezelOption,
                    onBezelOptionChange = { selectedBezelOption = it },
                    showBackground = showBackground,
                    onShowBackgroundChange = { showBackground = it },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Composable
fun EditorControls(
    screenMode: ScreenMode,
    onScreenModeChange: (ScreenMode) -> Unit,
    selectedBezelOption: BezelOption,
    onBezelOptionChange: (BezelOption) -> Unit,
    showBackground: Boolean,
    onShowBackgroundChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier.fillMaxWidth().testTag("editor_controls"), tonalElevation = 3.dp) {
        Row(
            modifier = Modifier.height(88.dp).padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ScreenMode.entries.forEach { mode ->
                    ScreenModeButton(mode, screenMode == mode) { onScreenModeChange(mode) }
                }
            }
            BezelPicker(
            selectedOption = selectedBezelOption,
            onOptionSelected = onBezelOptionChange
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Background", style = MaterialTheme.typography.labelSmall)
                Switch(
                    checked = showBackground,
                    onCheckedChange = onShowBackgroundChange,
                    modifier = Modifier.testTag("background_switch")
                )
            }
        }
    }
}

@Composable
private fun ScreenModeButton(mode: ScreenMode, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        modifier = Modifier
            .size(width = 44.dp, height = 58.dp)
            .testTag("screen_mode_${mode.name.lowercase()}")
            .semantics { this.selected = selected }
    ) {
        Box(contentAlignment = Alignment.Center) {
            val frameModifier = Modifier
                .height(if (mode == ScreenMode.OUTER) 36.dp else 27.dp)
                .aspectRatio(mode.iconAspectRatio)
            Box(
                modifier = frameModifier
                    .border(2.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(3.dp))
            ) {
                Box(
                    Modifier
                        .align(Alignment.TopCenter)
                        .offset(x = if (mode == ScreenMode.INNER) 8.dp else 0.dp)
                        .padding(top = if (mode == ScreenMode.OUTER) 3.dp else 4.dp)
                        .size(3.dp)
                        .background(MaterialTheme.colorScheme.onSurface, CircleShape)
                )
            }
        }
    }
}

@Composable
private fun BackgroundPalette(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        Color(0xFFF0F0F0), Color.White, Color(0xFF1D1D1F),
        Color(0xFFFFE8A3), Color(0xFFCDECCF), Color(0xFFCBE8F7)
    )
    Surface(modifier = modifier, shape = RoundedCornerShape(24.dp), tonalElevation = 6.dp, shadowElevation = 8.dp) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            colors.forEach { color ->
                Box(
                    Modifier
                        .size(34.dp)
                        .background(color, CircleShape)
                        .border(
                            if (selectedColor == color) 3.dp else 1.dp,
                            if (selectedColor == color) Color(0xFF2B83F6) else Color(0xFF777777),
                            CircleShape
                        )
                        .clickable { onColorSelected(color) }
                )
            }
        }
    }
}

@Composable
fun BezelPicker(
    selectedOption: BezelOption,
    onOptionSelected: (BezelOption) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        BezelOptions.forEach { option ->
            val isSelected = selectedOption == option
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onOptionSelected(option) }
                    .padding(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .then(
                            if (isSelected) {
                                Modifier
                                    .background(Color.White, CircleShape)
                                    .border(2.dp, Color(0xFF2B83F6), CircleShape)
                            } else {
                                Modifier
                            }
                        )
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(option.previewColor)
                            .border(
                                width = 1.dp,
                                color = Color(0xFF69696D),
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun MockupEditorScreenMobilePreview() {
    BezelTheme {
        MockupEditorScreen(
            uri = "https://example.com/image.jpg",
            onBack = {}
        )
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun MockupEditorScreenTabletPreview() {
    BezelTheme {
        MockupEditorScreen(
            uri = "https://example.com/image.jpg",
            onBack = {}
        )
    }
}
