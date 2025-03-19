package com.byteflipper.imageai.presentation.chat

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.byteflipper.imageai.data.chat.ChatMessage
import com.byteflipper.imageai.presentation.chat.components.LoadingIndicator
import com.byteflipper.imageai.presentation.chat.components.MessageItem

@Composable
fun MessageList(
    messages: List<ChatMessage>,
    isLoading: Boolean
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        reverseLayout = false
    ) {
        items(messages) { message ->
            MessageItem(message)
        }

        if (isLoading) {
            item {
                LoadingIndicator()
            }
        }
    }
}