package com.byteflipper.imageai.feature_chat.presentation.chat_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteflipper.imageai.core.domain.model.Chat
import com.byteflipper.imageai.core.domain.repository.ChatRepository
import com.byteflipper.imageai.core.presentation.ChatListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatListState())
    val uiState: StateFlow<ChatListState> = _uiState.asStateFlow()

    init {
        loadChats()
    }

    private fun loadChats() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            combine(
                chatRepository.getAllChats(),
                chatRepository.getArchivedChats()
            ) { regularChats, archivedChats ->
                if (_uiState.value.searchQuery.isBlank()) {
                    regularChats
                } else {
                    regularChats.filter { chat ->
                        chat.title.contains(_uiState.value.searchQuery, ignoreCase = true) ||
                        chat.lastMessagePreview.contains(_uiState.value.searchQuery, ignoreCase = true)
                    }
                }
            }.catch { throwable ->
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = throwable.message ?: "Неизвестная ошибка"
                    ) 
                }
            }.collect { chats ->
                _uiState.update { 
                    it.copy(
                        chats = chats,
                        isLoading = false,
                        error = null
                    ) 
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.isBlank()) {
            loadChats()
        } else {
            searchChats(query)
        }
    }

    fun onSearchActiveChanged(isActive: Boolean) {
        _uiState.update { it.copy(isSearchActive = isActive) }
        if (!isActive) {
            _uiState.update { it.copy(searchQuery = "") }
            loadChats()
        }
    }

    private fun searchChats(query: String) {
        viewModelScope.launch {
            chatRepository.searchChats(query)
                .catch { throwable ->
                    _uiState.update { 
                        it.copy(
                            error = throwable.message ?: "Ошибка поиска"
                        ) 
                    }
                }
                .collect { chats ->
                    _uiState.update { it.copy(chats = chats) }
                }
        }
    }

    fun createNewChat(): String {
        val chatId = UUID.randomUUID().toString()
        val now = Clock.System.now()
        
        val newChat = Chat(
            id = chatId,
            title = "Новый чат",
            lastMessagePreview = "",
            createdAt = now,
            updatedAt = now
        )

        viewModelScope.launch {
            try {
                chatRepository.createChat(newChat)
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = "Ошибка создания чата: ${e.message}") 
                }
            }
        }
        
        return chatId
    }

    fun deleteChat(chatId: String) {
        viewModelScope.launch {
            try {
                chatRepository.deleteChat(chatId)
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = "Ошибка удаления чата: ${e.message}") 
                }
            }
        }
    }

    fun archiveChat(chatId: String, isArchived: Boolean = true) {
        viewModelScope.launch {
            try {
                chatRepository.archiveChat(chatId, isArchived)
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = "Ошибка архивирования: ${e.message}") 
                }
            }
        }
    }

    fun pinChat(chatId: String, isPinned: Boolean) {
        viewModelScope.launch {
            try {
                chatRepository.pinChat(chatId, isPinned)
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = "Ошибка закрепления: ${e.message}") 
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
} 