package com.byteflipper.imageai.feature_chat.presentation.chat_detail.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun TypingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    dotCount: Int = 3,
    dotSize: Dp = 6.dp,
    animationDuration: Int = 600
) {
    val infiniteTransition = rememberInfiniteTransition(label = "typing_animation")
    
    Row(
        modifier = modifier.height(size),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(dotCount) { index ->
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = animationDuration,
                        easing = FastOutSlowInEasing,
                        delayMillis = index * (animationDuration / dotCount)
                    ),
                    repeatMode = RepeatMode.Reverse
                ), label = "dot_scale_$index"
            )
            
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = animationDuration,
                        easing = FastOutSlowInEasing,
                        delayMillis = index * (animationDuration / dotCount)
                    ),
                    repeatMode = RepeatMode.Reverse
                ), label = "dot_alpha_$index"
            )

            Box(
                modifier = Modifier
                    .size(dotSize)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    }
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
            )
        }
    }
} 