package com.example.bezel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.bezel.ui.Editor
import com.example.bezel.ui.Home
import com.example.bezel.ui.HomeScreen
import com.example.bezel.ui.MockupEditorScreen
import com.example.bezel.ui.Navigator
import com.example.bezel.ui.rememberNavigationState
import com.example.bezel.ui.theme.BezelTheme
import com.example.bezel.ui.toEntries

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BezelTheme {
                BezelApp()
            }
        }
    }
}

@Composable
fun BezelApp() {
    val navigationState = rememberNavigationState(
        startRoute = Home,
        topLevelRoutes = setOf(Home)
    )
    val navigator = remember { Navigator(navigationState) }

    val entryProvider = entryProvider {
        entry<Home> {
            HomeScreen(
                onImageSelected = { uri ->
                    navigator.navigate(Editor(uri))
                }
            )
        }
        entry<Editor> { key ->
            MockupEditorScreen(
                uri = key.uri,
                onBack = { navigator.goBack() }
            )
        }
    }

    NavDisplay(
        entries = navigationState.toEntries(entryProvider),
        onBack = { navigator.goBack() },
        modifier = Modifier.fillMaxSize()
    )
}
