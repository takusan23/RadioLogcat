package io.github.takusan23.radiologcat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.takusan23.radiologcat.ui.screen.MainScreen
import io.github.takusan23.radiologcat.ui.theme.RadioLogcatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RadioLogcatTheme {
                MainScreen()
            }
        }
    }
}