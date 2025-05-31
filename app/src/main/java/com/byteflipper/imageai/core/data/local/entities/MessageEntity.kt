package com.byteflipper.imageai.core.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = ChatEntity::class,
            parentColumns = ["id"],
            childColumns = ["chatId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["chatId"])]
)
data class MessageEntity(
    @PrimaryKey
    val id: String,
    val chatId: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Instant = Clock.System.now(),
    val imageBase64: String? = null,
    val isTyping: Boolean = false,
    val errorMessage: String? = null,
    val metadata: String? = null // JSON для дополнительных данных
) 