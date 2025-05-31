package com.byteflipper.imageai.core.presentation

sealed class UiState<out T> {
    object Initial : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : UiState<Nothing>()
}

sealed class ChatUiState {
    object Initial : ChatUiState()
    object Loading : ChatUiState()
    data class Typing(val partialResponse: String) : ChatUiState()
    data class Success(val response: String) : ChatUiState()
    data class Error(val message: String) : ChatUiState()
}

data class ChatListState(
    val chats: List<com.byteflipper.imageai.core.domain.model.Chat> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false
)

data class ChatDetailState(
    val chat: com.byteflipper.imageai.core.domain.model.Chat? = null,
    val messages: List<com.byteflipper.imageai.core.domain.model.Message> = emptyList(),
    val isLoading: Boolean = false,
    val isTyping: Boolean = false,
    val error: String? = null,
    val inputText: String = "",
    val selectedImage: android.graphics.Bitmap? = null
) 