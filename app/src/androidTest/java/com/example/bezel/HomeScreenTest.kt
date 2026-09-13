package com.example.bezel

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.bezel.ui.HomeScreen
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun modelLabelIsShownAtBottomOfHomeScreen() {
        composeRule.setContent {
            HomeScreen(onImageSelected = {})
        }

        composeRule
            .onNodeWithText("For Samsung Galaxy Z Fold 8")
            .assertIsDisplayed()
    }
}
