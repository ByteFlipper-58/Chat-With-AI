package com.byteflipper.imageai.core.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val lastMessagePreview: String,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now(),
    val isArchived: Boolean = false,
    val isPinned: Boolean = false,
    val messageCount: Int = 0
) 