package com.byteflipper.imageai.feature_chat.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.byteflipper.imageai.feature_chat.data.ChatMessage

@Composable
fun MessageList(
    messages: List<ChatMessage>,
    isLoading: Boolean
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        reverseLayout = false,
        verticalArrangement = Arrangement.Top
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