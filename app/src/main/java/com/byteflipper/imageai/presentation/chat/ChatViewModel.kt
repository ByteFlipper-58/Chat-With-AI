package com.byteflipper.imageai.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteflipper.imageai.BuildConfig
import com.byteflipper.imageai.UiState
import com.byteflipper.imageai.data.chat.ChatMessage
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.apiKey
    )

    fun sendTextMessage(text: String) {
        // Add user message
        addMessage(ChatMessage(
            text = text,
            isFromUser = true
        ))

        // Show loading state
        _uiState.value = UiState.Loading

        // Send to Gemini
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content {
                        text(text)
                    }
                )

                response.text?.let { outputContent ->
                    // Add AI response
                    addMessage(ChatMessage(
                        text = outputContent,
                        isFromUser = false
                    ))
                    _uiState.value = UiState.Success(outputContent)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Error processing request")

                // Add error message
                addMessage(ChatMessage(
                    text = "Sorry, I couldn't process that request: ${e.localizedMessage ?: "Unknown error"}",
                    isFromUser = false
                ))
            }
        }
    }

    fun sendImageMessage(bitmap: Bitmap, prompt: String) {
        // Add user message with image
        addMessage(ChatMessage(
            text = prompt,
            image = bitmap,
            isFromUser = true
        ))

        // Show loading state
        _uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content {
                        image(bitmap)
                        text(prompt)
                    }
                )

                response.text?.let { outputContent ->
                    // Add AI response
                    addMessage(ChatMessage(
                        text = outputContent,
                        isFromUser = false
                    ))
                    _uiState.value = UiState.Success(outputContent)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Error processing image")

                // Add error message
                addMessage(ChatMessage(
                    text = "Sorry, I couldn't process that image: ${e.localizedMessage ?: "Unknown error"}",
                    isFromUser = false
                ))
            }
        }
    }

    private fun addMessage(message: ChatMessage) {
        _messages.value = _messages.value + message
    }
}