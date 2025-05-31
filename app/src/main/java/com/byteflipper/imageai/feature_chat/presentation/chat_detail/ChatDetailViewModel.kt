package com.byteflipper.imageai.feature_chat.presentation.chat_detail

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteflipper.imageai.BuildConfig
import com.byteflipper.imageai.core.domain.model.Chat
import com.byteflipper.imageai.core.domain.model.Message
import com.byteflipper.imageai.core.domain.model.MessageStatus
import com.byteflipper.imageai.core.domain.repository.ChatRepository
import com.byteflipper.imageai.core.navigation.NavigationDestinations
import com.byteflipper.imageai.core.presentation.ChatDetailState
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val initialChatId: String = savedStateHandle.get<String>(NavigationDestinations.CHAT_ID_KEY) ?: "new"
    private var actualChatId: String = initialChatId
    
    private val _uiState = MutableStateFlow(ChatDetailState())
    val uiState: StateFlow<ChatDetailState> = _uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = "AIzaSyCeKo2RzNDSJ4pQzAc4E6FNnxj00_tdMgA",
        generationConfig = generationConfig {
            temperature = 0.8f
            topK = 40
            topP = 0.95f
            maxOutputTokens = 8000
        }
    )

    init {
        if (initialChatId == "new") {
            // Для нового чата просто устанавливаем пустое состояние
            _uiState.update { 
                it.copy(
                    isLoading = false,
                    messages = emptyList()
                )
            }
        } else {
            // Для существующего чата загружаем данные
            actualChatId = initialChatId
            loadChat()
            loadMessages()
        }
    }

    private suspend fun createNewChatIfNeeded(): String {
        if (actualChatId == "new") {
            // Создаем новый чат
            val newChat = com.byteflipper.imageai.core.domain.model.Chat(
                id = UUID.randomUUID().toString(),
                title = "Новый чат",
                lastMessagePreview = "",
                createdAt = Clock.System.now(),
                updatedAt = Clock.System.now(),
                isArchived = false,
                isPinned = false,
                messageCount = 0,
                messages = emptyList()
            )
            actualChatId = chatRepository.createChat(newChat)
        }
        return actualChatId
    }

    private fun loadChat() {
        viewModelScope.launch {
            chatRepository.getChatById(actualChatId)
                .catch { throwable ->
                    _uiState.update { 
                        it.copy(error = "Ошибка загрузки чата: ${throwable.message}")
                    }
                }
                .collect { chat ->
                    _uiState.update { it.copy(chat = chat) }
                }
        }
    }

    private fun loadMessages() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            chatRepository.getMessagesByChatId(actualChatId)
                .catch { throwable ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Ошибка загрузки сообщений: ${throwable.message}"
                        )
                    }
                }
                .collect { messages ->
                    _uiState.update { 
                        it.copy(
                            messages = messages,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    fun onInputTextChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun onImageSelected(bitmap: Bitmap?) {
        _uiState.update { it.copy(selectedImage = bitmap) }
    }

    fun sendTextMessage() {
        val inputText = _uiState.value.inputText.trim()
        if (inputText.isBlank()) return

        val userMessage = Message(
            id = UUID.randomUUID().toString(),
            chatId = actualChatId,
            content = inputText,
            isFromUser = true,
            timestamp = Clock.System.now(),
            status = MessageStatus.PENDING
        )

        // Очищаем поле ввода
        _uiState.update { it.copy(inputText = "") }

        sendMessage(userMessage, inputText)
    }

    fun sendImageMessage() {
        val inputText = _uiState.value.inputText.trim()
        val selectedImage = _uiState.value.selectedImage
        
        if (inputText.isBlank() || selectedImage == null) return

        val userMessage = Message(
            id = UUID.randomUUID().toString(),
            chatId = actualChatId,
            content = inputText,
            isFromUser = true,
            timestamp = Clock.System.now(),
            image = selectedImage,
            status = MessageStatus.PENDING
        )

        // Очищаем поля ввода
        _uiState.update { 
            it.copy(
                inputText = "",
                selectedImage = null
            ) 
        }

        sendMessage(userMessage, inputText, selectedImage)
    }

    private fun sendMessage(userMessage: Message, prompt: String, image: Bitmap? = null) {
        viewModelScope.launch {
            try {
                // Создаем новый чат если нужно
                val currentChatId = createNewChatIfNeeded()
                
                // Обновляем сообщение с правильным chatId
                val messageWithCorrectChatId = userMessage.copy(chatId = currentChatId)
                
                // Сохраняем сообщение пользователя
                chatRepository.sendMessage(messageWithCorrectChatId)
                
                // Если это первое сообщение, начинаем загружать чат и сообщения
                if (initialChatId == "new") {
                    loadChat()
                    loadMessages()
                }
                
                // Показываем индикатор печати
                _uiState.update { it.copy(isTyping = true) }

                var fullResponse = ""
                val responseMessageId = UUID.randomUUID().toString()
                
                // Создаем временное сообщение для ответа
                val tempBotMessage = Message(
                    id = responseMessageId,
                    chatId = currentChatId,
                    content = "",
                    isFromUser = false,
                    timestamp = Clock.System.now(),
                    isTyping = true
                )
                
                chatRepository.sendMessage(tempBotMessage)

                // Генерируем ответ от ИИ
                val contentBuilder = content {
                    if (image != null) {
                        image(image)
                    }
                    text(prompt)
                }

                generativeModel.generateContentStream(contentBuilder).collect { chunk ->
                    fullResponse += chunk.text ?: ""
                    
                    // Обновляем сообщение с частичным ответом
                    val updatedMessage = tempBotMessage.copy(
                        content = fullResponse,
                        isTyping = false
                    )
                    chatRepository.updateMessage(updatedMessage)
                }

                _uiState.update { it.copy(isTyping = false) }

            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isTyping = false,
                        error = "Ошибка отправки сообщения: ${e.localizedMessage}"
                    )
                }
                
                // Получаем правильный chatId для сообщения об ошибке
                val currentChatId = if (actualChatId == "new") {
                    try {
                        createNewChatIfNeeded()
                    } catch (ex: Exception) {
                        actualChatId
                    }
                } else {
                    actualChatId
                }
                
                // Добавляем сообщение об ошибке
                val errorMessage = Message(
                    id = UUID.randomUUID().toString(),
                    chatId = currentChatId,
                    content = "Извините, произошла ошибка: ${e.localizedMessage}",
                    isFromUser = false,
                    timestamp = Clock.System.now(),
                    status = MessageStatus.ERROR,
                    errorMessage = e.localizedMessage
                )
                
                chatRepository.sendMessage(errorMessage)
            }
        }
    }

    fun retryMessage(messageId: String) {
        viewModelScope.launch {
            val message = _uiState.value.messages.find { it.id == messageId }
            if (message != null && message.isFromUser) {
                sendMessage(
                    message.copy(status = MessageStatus.PENDING),
                    message.content,
                    message.image
                )
            }
        }
    }

    fun deleteMessage(messageId: String) {
        viewModelScope.launch {
            try {
                chatRepository.deleteMessage(messageId)
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = "Ошибка удаления сообщения: ${e.message}")
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
} 