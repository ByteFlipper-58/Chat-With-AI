package com.byteflipper.imageai.feature_chat.presentation.chat_list

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.byteflipper.imageai.R
import com.byteflipper.imageai.core.theme.*
import com.byteflipper.imageai.feature_chat.presentation.chat_list.components.ChatListItem
import com.byteflipper.imageai.feature_chat.presentation.chat_list.components.EmptyChatsState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatListScreen(
    onChatClick: (String) -> Unit,
    onNewChatClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChatListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Анимация для FAB
    val fabScale by animateFloatAsState(
        targetValue = if (uiState.isSearchActive) 0.0f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = "fab_scale"
    )

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Material 3 SearchBar
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChanged,
                    onSearch = { viewModel.onSearchActiveChanged(false) },
                    expanded = uiState.isSearchActive,
                    onExpandedChange = viewModel::onSearchActiveChanged,
                    placeholder = {
                        Text(
                            text = "Поиск чатов...",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.search_24px),
                            contentDescription = "Поиск",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = if (uiState.searchQuery.isNotEmpty()) {
                        {
                            IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.close_24px),
                                    contentDescription = "Очистить",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else null
                )
            },
            expanded = uiState.isSearchActive,
            onExpandedChange = viewModel::onSearchActiveChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = if (uiState.isSearchActive) 0.dp else 16.dp)
                .padding(top = 8.dp),
            windowInsets = WindowInsets.statusBars
        ) {
            // Результаты поиска или контент в развернутом состоянии
            if (uiState.searchQuery.isNotEmpty()) {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filteredChats = uiState.chats.filter { chat ->
                        chat.title.contains(uiState.searchQuery, ignoreCase = true) ||
                        chat.lastMessagePreview.contains(uiState.searchQuery, ignoreCase = true)
                    }
                    
                    if (filteredChats.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.search_24px),
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Ничего не найдено",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Попробуйте изменить запрос",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(
                            items = filteredChats,
                            key = { it.id }
                        ) { chat ->
                            ChatListItem(
                                chat = chat,
                                onClick = { 
                                    onChatClick(chat.id)
                                    viewModel.onSearchActiveChanged(false)
                                },
                                onPin = { viewModel.pinChat(chat.id, !chat.isPinned) },
                                onArchive = { viewModel.archiveChat(chat.id) },
                                onDelete = { viewModel.deleteChat(chat.id) }
                            )
                        }
                    }
                }
            }
        }

        // Основной контент когда поиск не активен
        AnimatedVisibility(
            visible = !uiState.isSearchActive,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "История чатов",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                
                                Spacer(modifier = Modifier.weight(1f))
                                
                                IconButton(onClick = onSettingsClick) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.settings_24px),
                                        contentDescription = "Настройки",
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            titleContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                },
                floatingActionButton = {
                    AnimatedVisibility(
                        visible = fabScale > 0f,
                        enter = scaleIn() + fadeIn(),
                        exit = scaleOut() + fadeOut()
                    ) {
                        FloatingActionButton(
                            onClick = onNewChatClick,
                            modifier = Modifier.scale(fabScale),
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.add_24px),
                                contentDescription = "Новый чат"
                            )
                        }
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surface,
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                )
                            )
                        )
                ) {
                    when {
                        uiState.isLoading -> {
                            LoadingState(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        
                        uiState.chats.isEmpty() && !uiState.isLoading -> {
                            EmptyChatsState(
                                onCreateFirstChat = onNewChatClick,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        
                        else -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Закрепленные чаты
                                val pinnedChats = uiState.chats.filter { it.isPinned }
                                val regularChats = uiState.chats.filter { !it.isPinned }
                                
                                if (pinnedChats.isNotEmpty()) {
                                    item {
                                        Text(
                                            text = "Закрепленные",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                                        )
                                    }
                                    
                                    items(
                                        items = pinnedChats,
                                        key = { it.id }
                                    ) { chat ->
                                        ChatListItem(
                                            chat = chat,
                                            onClick = { onChatClick(chat.id) },
                                            onPin = { viewModel.pinChat(chat.id, !chat.isPinned) },
                                            onArchive = { viewModel.archiveChat(chat.id) },
                                            onDelete = { viewModel.deleteChat(chat.id) }
                                        )
                                    }
                                    
                                    if (regularChats.isNotEmpty()) {
                                        item {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "Остальные",
                                                style = MaterialTheme.typography.titleSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                                            )
                                        }
                                    }
                                }
                                
                                items(
                                    items = regularChats,
                                    key = { it.id }
                                ) { chat ->
                                    ChatListItem(
                                        chat = chat,
                                        onClick = { onChatClick(chat.id) },
                                        onPin = { viewModel.pinChat(chat.id, !chat.isPinned) },
                                        onArchive = { viewModel.archiveChat(chat.id) },
                                        onDelete = { viewModel.deleteChat(chat.id) }
                                    )
                                }
                            }
                        }
                    }

                    // Отображение ошибок
                    uiState.error?.let { error ->
                        LaunchedEffect(error) {
                            // Можно показать SnackBar или другое уведомление
                        }
                    }
                }
            }
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
            text = "Загрузка чатов...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
} 