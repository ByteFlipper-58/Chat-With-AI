package com.byteflipper.imageai.feature_chat.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.byteflipper.imageai.feature_chat.presentation.components.ChatScaffold

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    chatViewModel: ChatViewModel = viewModel()
) {
    ChatScaffold(
        modifier = modifier,
        chatViewModel = chatViewModel,
        title = "Gemini"
    )
}