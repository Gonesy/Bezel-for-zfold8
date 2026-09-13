package com.example.bezel

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.platform.LocalContext
import com.example.bezel.ui.MockupEditorScreen
import com.example.bezel.ui.theme.BezelTheme
import org.junit.Rule
import org.junit.Test

class MockupEditorInteractionTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun tappingPreviewTogglesFullscreenEditor() {
        composeRule.setContent {
            BezelTheme {
                MockupEditorScreen(uri = "", onBack = {})
            }
        }

        composeRule.onNodeWithTag("editor_controls").assertIsDisplayed()
        composeRule.onNodeWithTag("mockup_preview").performClick()
        composeRule.onNodeWithTag("editor_controls").assertDoesNotExist()
        composeRule.onNodeWithTag("mockup_preview").performClick()
        composeRule.onNodeWithTag("editor_controls").assertIsDisplayed()
    }

    @Test
    fun imageDimensionsAutomaticallySelectClosestScreenMode() {
        composeRule.setContent {
            val context = LocalContext.current
            BezelTheme {
                MockupEditorScreen(
                    uri = "android.resource://${context.packageName}/${R.drawable.placeholder}",
                    onBack = {}
                )
            }
        }

        composeRule.waitUntil(timeoutMillis = 5_000) {
            runCatching {
                composeRule.onNodeWithTag("screen_mode_inner").assertIsSelected()
                true
            }.getOrDefault(false)
        }
        composeRule.onNodeWithTag("screen_mode_inner").assertIsSelected()
    }
}
