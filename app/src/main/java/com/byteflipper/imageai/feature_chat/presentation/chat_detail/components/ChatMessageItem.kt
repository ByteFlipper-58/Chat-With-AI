package com.byteflipper.imageai.feature_chat.presentation.chat_detail.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.byteflipper.imageai.R
import com.byteflipper.imageai.core.domain.model.Message
import com.byteflipper.imageai.core.domain.model.MessageStatus
import com.byteflipper.imageai.core.theme.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatMessageItem(
    message: Message,
    onRetry: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromUser) {
            Arrangement.End
        } else {
            Arrangement.Start
        }
    ) {
        // Сообщение
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .combinedClickable(
                    onClick = { },
                    onLongClick = { showMenu = true }
                )
        ) {
            if (message.isFromUser) {
                // Сообщение пользователя с фоном
                Card(
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 4.dp,
                        bottomStart = 16.dp,
                        bottomEnd = 16.dp
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 4.dp
                    )
                ) {
                    MessageContent(
                        message = message,
                        isFromUser = true,
                        onRetry = onRetry
                    )
                }
            } else {
                // Сообщение ИИ без фона, только текст
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    // Изображение, если есть
                    message.image?.let { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        
                        if (message.content.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    // Текст сообщения
                    if (message.content.isNotBlank()) {
                        Text(
                            text = message.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Время и статус
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatMessageTime(message.timestamp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }

                    // Индикатор печати
                    if (message.isTyping) {
                        Spacer(modifier = Modifier.height(4.dp))
                        TypingIndicator(
                            size = 16.dp,
                            dotCount = 3
                        )
                    }

                    // Сообщение об ошибке
                    message.errorMessage?.let { error ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Ошибка: $error",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Выпадающее меню
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                if (message.status == MessageStatus.ERROR) {
                    DropdownMenuItem(
                        text = { Text("Повторить") },
                        onClick = {
                            showMenu = false
                            onRetry()
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.redo_24px),
                                contentDescription = null
                            )
                        }
                    )
                }
                
                DropdownMenuItem(
                    text = { 
                        Text(
                            "Удалить",
                            color = MaterialTheme.colorScheme.error
                        ) 
                    },
                    onClick = {
                        showMenu = false
                        onDelete()
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.auto_delete_24px),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun MessageContent(
    message: Message,
    isFromUser: Boolean,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.padding(12.dp)
    ) {
        // Изображение, если есть
        message.image?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            if (message.content.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Текст сообщения
        if (message.content.isNotBlank()) {
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        // Время и статус
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatMessageTime(message.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.width(4.dp))
            
            when (message.status) {
                MessageStatus.PENDING -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(12.dp),
                        strokeWidth = 1.dp,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    )
                }
                MessageStatus.SENT -> {
                    Icon(
                        painter = painterResource(id = R.drawable.check_24px),
                        contentDescription = "Отправлено",
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    )
                }
                MessageStatus.DELIVERED -> {
                    Icon(
                        painter = painterResource(id = R.drawable.done_all_24px),
                        contentDescription = "Доставлено",
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    )
                }
                MessageStatus.ERROR -> {
                    IconButton(
                        onClick = onRetry,
                        modifier = Modifier.size(16.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.redo_24px),
                            contentDescription = "Повторить",
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        // Индикатор печати
        if (message.isTyping) {
            Spacer(modifier = Modifier.height(4.dp))
            TypingIndicator(
                size = 16.dp,
                dotCount = 3
            )
        }

        // Сообщение об ошибке
        message.errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Ошибка: $error",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun formatMessageTime(timestamp: kotlinx.datetime.Instant): String {
    val localDateTime = timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
    return String.format("%02d:%02d", localDateTime.hour, localDateTime.minute)
} 