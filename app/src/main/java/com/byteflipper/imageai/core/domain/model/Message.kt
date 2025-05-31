package com.byteflipper.imageai.core.domain.model

import android.graphics.Bitmap
import kotlinx.datetime.Instant

data class Message(
    val id: String,
    val chatId: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Instant,
    val image: Bitmap? = null,
    val isTyping: Boolean = false,
    val errorMessage: String? = null,
    val status: MessageStatus = MessageStatus.SENT
)

enum class MessageStatus {
    PENDING,
    SENT,
    DELIVERED,
    ERROR
} 