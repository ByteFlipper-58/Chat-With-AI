package com.byteflipper.imageai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.byteflipper.imageai.ui.components.ChatScreen
import com.byteflipper.imageai.ui.theme.ImageAITheme
import com.byteflipper.imageai.viewmodel.ChatViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ImageAITheme {
                Scaffold()
                { paddingValues ->
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background,
                    ) {
                        ChatScreen(modifier = Modifier.padding(paddingValues))
                    }
                }
            }
        }
    }
}
