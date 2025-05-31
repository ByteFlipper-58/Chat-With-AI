package com.byteflipper.imageai.feature_chat.presentation.chat_detail

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.byteflipper.imageai.R
import com.byteflipper.imageai.core.theme.*
import com.byteflipper.imageai.feature_chat.presentation.chat_detail.components.ChatMessageItem
import com.byteflipper.imageai.feature_chat.presentation.chat_detail.components.ChatInput
import com.byteflipper.imageai.feature_chat.presentation.chat_detail.components.TypingIndicator
import com.byteflipper.imageai.feature_chat.presentation.chat_detail.components.EmptyMessagesState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatDetailScreen(
    onBackClick: () -> Unit,
    onHistoryClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChatDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    // Автоматически прокручиваем к последнему сообщению
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = uiState.chat?.title ?: "Новый чат",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        if (uiState.isTyping) {
                            Text(
                                text = "печатает...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else if (uiState.messages.isNotEmpty()) {
                            Text(
                                text = "${uiState.messages.size} сообщений",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(
                                text = "Начните общение с ИИ",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onHistoryClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.history_24px),
                            contentDescription = "История чатов",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    // Кнопка создания нового чата
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.add_24px),
                            contentDescription = "Новый чат",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    IconButton(onClick = { /* TODO: Меню чата */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.more_vert_24px),
                            contentDescription = "Меню",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Список сообщений
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
                            )
                        )
                    )
            ) {
                when {
                    uiState.isLoading && uiState.messages.isEmpty() -> {
                        LoadingState(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    
                    uiState.messages.isEmpty() && !uiState.isLoading -> {
                        EmptyMessagesState(
                            chatTitle = uiState.chat?.title ?: "Чат",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    
                    else -> {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = uiState.messages,
                                key = { it.id }
                            ) { message ->
                                ChatMessageItem(
                                    message = message,
                                    onRetry = { viewModel.retryMessage(message.id) },
                                    onDelete = { viewModel.deleteMessage(message.id) }
                                )
                            }
                            
                            // Индикатор печати
                            if (uiState.isTyping) {
                                item {
                                    TypingIndicator(
                                        modifier = Modifier.padding(
                                            start = 16.dp,
                                            top = 8.dp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Отображение ошибок
                uiState.error?.let { error ->
                    LaunchedEffect(error) {
                        // Можно показать SnackBar
                    }
                }
            }

            // Поле ввода
            ChatInput(
                inputText = uiState.inputText,
                onInputTextChanged = viewModel::onInputTextChanged,
                selectedImage = uiState.selectedImage,
                onImageSelected = viewModel::onImageSelected,
                onSendTextMessage = viewModel::sendTextMessage,
                onSendImageMessage = viewModel::sendImageMessage,
                isEnabled = !uiState.isTyping,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
            )
        }
    }
}

@Composable
private fun LoadingState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Загрузка сообщений...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
} 