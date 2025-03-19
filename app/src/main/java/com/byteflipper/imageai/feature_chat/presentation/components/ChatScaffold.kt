package com.byteflipper.imageai.feature_chat.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.byteflipper.imageai.core.model.UiState
import com.byteflipper.imageai.feature_chat.presentation.ChatViewModel

@Composable
fun ChatScaffold(
    modifier: Modifier = Modifier,
    chatViewModel: ChatViewModel = viewModel(),
    title: String = "Gemini"
) {
    val uiState by chatViewModel.uiState.collectAsState()
    val messages by chatViewModel.messages.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            ChatToolbar(title = title)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
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

            ChatInput(
                onSendMessage = { message ->
                    chatViewModel.sendTextMessage(message)
                },
                onAttachImage = { /* TODO: Добавить обработку */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
            )
        }
    }
}