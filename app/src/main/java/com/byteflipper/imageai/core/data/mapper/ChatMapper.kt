package com.byteflipper.imageai.core.data.mapper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.byteflipper.imageai.core.data.local.entities.ChatEntity
import com.byteflipper.imageai.core.data.local.entities.MessageEntity
import com.byteflipper.imageai.core.domain.model.Chat
import com.byteflipper.imageai.core.domain.model.Message
import com.byteflipper.imageai.core.domain.model.MessageStatus
import java.io.ByteArrayOutputStream

// Конвертация Chat -> ChatEntity
fun Chat.toEntity(): ChatEntity {
    return ChatEntity(
        id = id,
        title = title,
        lastMessagePreview = lastMessagePreview,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isArchived = isArchived,
        isPinned = isPinned,
        messageCount = messageCount
    )
}

// Конвертация ChatEntity -> Chat
fun ChatEntity.toDomain(messages: List<Message> = emptyList()): Chat {
    return Chat(
        id = id,
        title = title,
        lastMessagePreview = lastMessagePreview,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isArchived = isArchived,
        isPinned = isPinned,
        messageCount = messageCount,
        messages = messages
    )
}

// Конвертация Message -> MessageEntity
fun Message.toEntity(): MessageEntity {
    return MessageEntity(
        id = id,
        chatId = chatId,
        content = content,
        isFromUser = isFromUser,
        timestamp = timestamp,
        imageBase64 = image?.toBase64(),
        isTyping = isTyping,
        errorMessage = errorMessage
    )
}

// Конвертация MessageEntity -> Message
fun MessageEntity.toDomain(): Message {
    return Message(
        id = id,
        chatId = chatId,
        content = content,
        isFromUser = isFromUser,
        timestamp = timestamp,
        image = imageBase64?.toBitmap(),
        isTyping = isTyping,
        errorMessage = errorMessage,
        status = if (errorMessage != null) MessageStatus.ERROR else MessageStatus.SENT
    )
}

// Helper функции для работы с изображениями
private fun Bitmap.toBase64(): String {
    val outputStream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
    val byteArray = outputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

private fun String.toBitmap(): Bitmap? {
    return try {
        val decodedBytes = Base64.decode(this, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        null
    }
} 