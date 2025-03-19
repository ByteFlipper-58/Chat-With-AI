package com.byteflipper.imageai.feature_chat.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.byteflipper.imageai.core.model.UiState
import com.byteflipper.imageai.feature_chat.presentation.ChatViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

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
            ChatToolbar(title = title) // Тулбар фиксированный
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Учитываем отступы Scaffold
        ) {
            Box(
                modifier = Modifier
                    .weight(1f) // Контент занимает всё пространство
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (messages.isEmpty()) {
                    EmptyChat() // Теперь в центре экрана
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
                onAttachImage = { /* TODO: Добавить обработку */ }
            )
        }
    }
}