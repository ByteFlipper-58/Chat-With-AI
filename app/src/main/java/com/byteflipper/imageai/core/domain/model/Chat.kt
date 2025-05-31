package com.byteflipper.imageai.core.domain.model

import kotlinx.datetime.Instant

data class Chat(
    val id: String,
    val title: String,
    val lastMessagePreview: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val isArchived: Boolean = false,
    val isPinned: Boolean = false,
    val messageCount: Int = 0,
    val messages: List<Message> = emptyList()
) 