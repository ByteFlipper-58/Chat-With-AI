package com.byteflipper.imageai.feature_chat.presentation.chat_list.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.byteflipper.imageai.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    isActive: Boolean,
    onActiveChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Поиск чатов..."
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(isActive) {
        if (isActive) {
            focusRequester.requestFocus()
        } else {
            keyboardController?.hide()
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isActive) 4.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Иконка поиска/назад
            AnimatedContent(
                targetState = isActive,
                transitionSpec = {
                    slideInHorizontally { -it } + fadeIn() with
                    slideOutHorizontally { -it } + fadeOut()
                }, label = "search_icon"
            ) { active ->
                IconButton(
                    onClick = {
                        if (active) {
                            onActiveChange(false)
                            onQueryChange("")
                        } else {
                            onActiveChange(true)
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (active) R.drawable.arrow_back_24px else R.drawable.search_24px
                        ),
                        contentDescription = if (active) "Назад" else "Поиск",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Поле ввода
            TextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                placeholder = {
                    Text(
                        text = placeholder,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        keyboardController?.hide()
                    }
                )
            )

            // Кнопка очистки
            AnimatedVisibility(
                visible = query.isNotEmpty(),
                enter = slideInHorizontally { it } + fadeIn(),
                exit = slideOutHorizontally { it } + fadeOut()
            ) {
                IconButton(
                    onClick = { onQueryChange("") }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.close_24px),
                        contentDescription = "Очистить",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
} 