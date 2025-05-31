package com.byteflipper.imageai.core.domain.repository

import com.byteflipper.imageai.core.domain.model.Chat
import com.byteflipper.imageai.core.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    
    fun getAllChats(): Flow<List<Chat>>
    fun getArchivedChats(): Flow<List<Chat>>
    fun getChatById(chatId: String): Flow<Chat?>
    fun getMessagesByChatId(chatId: String): Flow<List<Message>>
    
    suspend fun createChat(chat: Chat): String
    suspend fun updateChat(chat: Chat)
    suspend fun deleteChat(chatId: String)
    suspend fun archiveChat(chatId: String, isArchived: Boolean)
    suspend fun pinChat(chatId: String, isPinned: Boolean)
    
    suspend fun sendMessage(message: Message)
    suspend fun updateMessage(message: Message)
    suspend fun deleteMessage(messageId: String)
    
    fun searchChats(query: String): Flow<List<Chat>>
    fun searchMessages(query: String): Flow<List<Message>>
    fun searchMessagesInChat(chatId: String, query: String): Flow<List<Message>>
} 