package com.byteflipper.imageai.feature_chat.data

import android.graphics.Bitmap
import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val image: Bitmap? = null,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)