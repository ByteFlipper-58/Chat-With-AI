package com.byteflipper.imageai.feature_chat.presentation.chat_list.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.byteflipper.imageai.R
import com.byteflipper.imageai.core.domain.model.Chat
import com.byteflipper.imageai.core.theme.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChatListItem(
    chat: Chat,
    onClick: () -> Unit,
    onPin: () -> Unit,
    onArchive: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ), label = "item_scale"
    )

    val backgroundColor = if (chat.isPinned) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .combinedClickable(
                onClick = onClick,
                onLongClick = { showMenu = true }
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (chat.isPinned) 4.dp else 2.dp,
            pressedElevation = 8.dp
        )
    ) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Аватар чата
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ai),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Информация о чате
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = chat.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        
                        if (chat.isPinned) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.bookmark_24px),
                                contentDescription = "Закреплен",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    if (chat.lastMessagePreview.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = chat.lastMessagePreview,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatTimestamp(chat.updatedAt),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        if (chat.messageCount > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${chat.messageCount} сообщений",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Кнопка меню
                IconButton(
                    onClick = { showMenu = true }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.more_vert_24px),
                        contentDescription = "Меню",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Выпадающее меню
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                DropdownMenuItem(
                    text = {
                        Text(if (chat.isPinned) "Открепить" else "Закрепить")
                    },
                    onClick = {
                        showMenu = false
                        onPin()
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(
                                id = if (chat.isPinned) R.drawable.bookmark_24px else R.drawable.bookmark_24px
                            ),
                            contentDescription = null
                        )
                    }
                )
                
                DropdownMenuItem(
                    text = { Text("Архивировать") },
                    onClick = {
                        showMenu = false
                        onArchive()
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.inventory_2_24px),
                            contentDescription = null
                        )
                    }
                )
                
                Divider()
                
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
private fun formatTimestamp(timestamp: kotlinx.datetime.Instant): String {
    val context = LocalContext.current
    val localDateTime = timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
    val now = kotlinx.datetime.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    
    return when {
        localDateTime.date == now.date -> {
            // Сегодня - показываем время
            String.format("%02d:%02d", localDateTime.hour, localDateTime.minute)
        }
        localDateTime.date == now.date.minus(kotlinx.datetime.DatePeriod(days = 1)) -> {
            // Вчера
            "Вчера"
        }
        else -> {
            // Другие дни - показываем дату
            String.format("%02d.%02d.%04d", 
                localDateTime.dayOfMonth, 
                localDateTime.monthNumber, 
                localDateTime.year
            )
        }
    }
} 