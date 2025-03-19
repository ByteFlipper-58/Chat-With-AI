package com.byteflipper.imageai.presentation.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.byteflipper.imageai.UiState
import com.byteflipper.imageai.presentation.chat.components.EmptyChat
import com.byteflipper.imageai.viewmodel.ChatViewModel

@Composable
fun ChatScaffold(
    modifier: Modifier = Modifier,
    chatViewModel: ChatViewModel = viewModel(),
    title: String = "Gemini"
) {
    val uiState by chatViewModel.uiState.collectAsState()
    val messages by chatViewModel.messages.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            ChatToolbar(title = title)
        },
        bottomBar = {
            ChatInput(
                onSendMessage = { message ->
                    chatViewModel.sendTextMessage(message)
                },
                onAttachImage = { /* To be implemented */ }
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                if (messages.isEmpty()) {
                    EmptyChat()
                } else {
                    MessageList(
                        messages = messages,
                        isLoading = uiState is UiState.Loading
                    )
                }
            }
        }
    }
}