package com.byteflipper.imageai.feature_chat.presentation

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteflipper.imageai.BuildConfig
import com.byteflipper.imageai.core.model.UiState
import com.byteflipper.imageai.feature_chat.data.ChatMessage
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.apiKey,
        generationConfig = generationConfig {
            temperature = 0.8f
            topK = 40
            topP = 0.95f
            maxOutputTokens = 8000
        }
    )

    fun sendTextMessage(text: String) {
        addMessage(ChatMessage(text = text, isFromUser = true))
        _uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                var fullResponse = ""
                generativeModel.generateContentStream(content { text(text) }).collect { chunk ->
                    fullResponse += chunk.text
                    _uiState.value = UiState.Success(fullResponse)
                    updateLastMessage(fullResponse)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Error processing request")
                addMessage(ChatMessage(text = "Sorry, an error occurred: ${e.localizedMessage}", isFromUser = false))
            }
        }
    }

    fun sendImageMessage(bitmap: Bitmap, prompt: String) {
        addMessage(ChatMessage(text = prompt, image = bitmap, isFromUser = true))
        _uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                var fullResponse = ""
                generativeModel.generateContentStream(
                    content {
                        image(bitmap)
                        text(prompt)
                    }
                ).collect { chunk ->
                    fullResponse += chunk.text
                    _uiState.value = UiState.Success(fullResponse)
                    updateLastMessage(fullResponse)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Error processing image")
                addMessage(ChatMessage(text = "Sorry, an error occurred: ${e.localizedMessage}", isFromUser = false))
            }
        }
    }

    private fun addMessage(message: ChatMessage) {
        _messages.value = _messages.value + message
    }

    private fun updateLastMessage(newText: String) {
        val updatedMessages = _messages.value.toMutableList()
        if (updatedMessages.isNotEmpty() && !updatedMessages.last().isFromUser) {
            updatedMessages[updatedMessages.lastIndex] = updatedMessages.last().copy(text = newText)
            _messages.value = updatedMessages
        } else {
            addMessage(ChatMessage(text = newText, isFromUser = false))
        }
    }
}