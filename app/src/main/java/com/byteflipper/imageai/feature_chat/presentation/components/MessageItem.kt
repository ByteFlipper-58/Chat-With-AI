package com.byteflipper.imageai.feature_chat.presentation.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.togetherWith
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.byteflipper.imageai.R
import com.byteflipper.imageai.feature_chat.data.ChatMessage

@Composable
fun UserMessageBubble(message: ChatMessage) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.widthIn(max = 300.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            message.image?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Attached image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                )
            }
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun MessageItem(message: ChatMessage) {
    val isUser = message.isFromUser
    val alignment = if (isUser) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = alignment
    ) {
        if (!isUser) {
            Image(
                painter = painterResource(id = R.drawable.ai),
                contentDescription = "AI Icon",
                modifier = Modifier
                    .size(24.dp)
                    .offset(x = 16.dp, y = 0.dp)
            )
        }

        if (isUser) {
            UserMessageBubble(message)
        } else {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.Transparent,
                modifier = Modifier.widthIn(max = 300.dp)
            ) {
                Column(modifier = Modifier.padding(start = 8.dp, top = 8.dp).offset(x = 0.dp, y = 4.dp)) {
                    message.image?.let { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Attached image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                    AnimatedContent(
                        targetState = message.text,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(durationMillis = 300)) togetherWith
                                    fadeOut(animationSpec = tween(durationMillis = 150))
                        }, label = "text_animation"
                    ) { targetText ->
                        Text(
                            text = targetText,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        if (!isUser) {
            Row(
                modifier = Modifier.padding(top = 2.dp),
            ) {
                IconButton(onClick = { /* TODO: Поделиться */ }) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = R.drawable.share_24px),
                        contentDescription = "Localized description"
                    )
                }
                IconButton(onClick = { /* TODO: Копировать */ }) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = R.drawable.content_copy_24px),
                        contentDescription = "Copy"
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MessageItemPreview() {
    val context = LocalContext.current
    val imageBitmap: Bitmap? = BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_background)

    val sampleMessage = ChatMessage(
        text = "Hello, this is a test message.",
        isFromUser = false,
        image = imageBitmap
    )
    val userSampleMessage = ChatMessage(
        text = "Hello, this is a user test message.",
        isFromUser = true,
    )
    Column { MessageItem(sampleMessage); MessageItem(userSampleMessage) }
}