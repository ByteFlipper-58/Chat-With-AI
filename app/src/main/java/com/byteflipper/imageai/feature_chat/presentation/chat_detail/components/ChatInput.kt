package com.byteflipper.imageai.feature_chat.presentation.chat_detail.components

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.byteflipper.imageai.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ChatInput(
    inputText: String,
    onInputTextChanged: (String) -> Unit,
    selectedImage: Bitmap?,
    onImageSelected: (Bitmap?) -> Unit,
    onSendTextMessage: () -> Unit,
    onSendImageMessage: () -> Unit,
    isEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    
    // Launcher для выбора изображения
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val imageLoader = ImageLoader(context)
                    val request = ImageRequest.Builder(context)
                        .data(uri)
                        .build()
                    
                    val result = imageLoader.execute(request)
                    if (result is SuccessResult) {
                        val bitmap = result.drawable.toBitmap()
                        CoroutineScope(Dispatchers.Main).launch {
                            onImageSelected(bitmap)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
    
    val hasContent = inputText.isNotBlank() || selectedImage != null
    
    // Анимации
    val sendButtonScale by animateFloatAsState(
        targetValue = if (hasContent) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ), label = "send_button_scale"
    )
    
    val containerColor by animateColorAsState(
        targetValue = if (hasContent) {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy),
        label = "container_color"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Предварительный просмотр изображения
        AnimatedVisibility(
            visible = selectedImage != null,
            enter = slideInVertically(
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            ) + fadeIn(),
            exit = slideOutVertically(
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            ) + fadeOut()
        ) {
            selectedImage?.let { bitmap ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Выбранное изображение",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        
                        // Кнопка удаления изображения
                        IconButton(
                            onClick = { onImageSelected(null) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(32.dp)
                                .background(
                                    color = Color.Black.copy(alpha = 0.6f),
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.close_24px),
                                contentDescription = "Удалить изображение",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
        
        // Основная панель ввода
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = containerColor,
            shadowElevation = if (hasContent) 4.dp else 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                // Кнопка выбора изображения
                IconButton(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    enabled = isEnabled,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.image_24px),
                        contentDescription = "Выбрать изображение",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Поле ввода текста
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = if (hasContent) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            } else {
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            },
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    BasicTextField(
                        value = inputText,
                        onValueChange = onInputTextChanged,
                        enabled = isEnabled,
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        decorationBox = { innerTextField ->
                            if (inputText.isEmpty()) {
                                Text(
                                    text = if (selectedImage != null) {
                                        "Опишите изображение..."
                                    } else {
                                        "Введите сообщение..."
                                    },
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                            innerTextField()
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Send
                        ),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (hasContent) {
                                    if (selectedImage != null) {
                                        onSendImageMessage()
                                    } else {
                                        onSendTextMessage()
                                    }
                                    keyboardController?.hide()
                                }
                            }
                        )
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Динамическая кнопка отправки
                AnimatedContent(
                    targetState = hasContent,
                    transitionSpec = {
                        scaleIn(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessHigh
                            )
                        ) with scaleOut(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessHigh
                            )
                        )
                    }, label = "send_button_animation"
                ) { hasContentState ->
                    if (hasContentState) {
                        FloatingActionButton(
                            onClick = {
                                if (selectedImage != null) {
                                    onSendImageMessage()
                                } else {
                                    onSendTextMessage()
                                }
                                keyboardController?.hide()
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .scale(sendButtonScale),
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            elevation = FloatingActionButtonDefaults.elevation(
                                defaultElevation = 6.dp,
                                pressedElevation = 10.dp
                            )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.send_24px),
                                contentDescription = "Отправить",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else {
                        // Плейсхолдер для сохранения размера
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.send_24px),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
            }
        }
    }
} 