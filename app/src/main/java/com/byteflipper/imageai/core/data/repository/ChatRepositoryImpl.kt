package com.byteflipper.imageai.core.data.repository

import com.byteflipper.imageai.core.data.local.dao.ChatDao
import com.byteflipper.imageai.core.data.local.dao.MessageDao
import com.byteflipper.imageai.core.data.mapper.toDomain
import com.byteflipper.imageai.core.data.mapper.toEntity
import com.byteflipper.imageai.core.domain.model.Chat
import com.byteflipper.imageai.core.domain.model.Message
import com.byteflipper.imageai.core.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao,
    private val messageDao: MessageDao
) : ChatRepository {

    override fun getAllChats(): Flow<List<Chat>> {
        return chatDao.getAllChats().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getArchivedChats(): Flow<List<Chat>> {
        return chatDao.getArchivedChats().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getChatById(chatId: String): Flow<Chat?> {
        return combine(
            chatDao.getChatByIdFlow(chatId),
            messageDao.getMessagesByChatId(chatId)
        ) { chatEntity, messageEntities ->
            chatEntity?.toDomain(messageEntities.map { it.toDomain() })
        }
    }

    override fun getMessagesByChatId(chatId: String): Flow<List<Message>> {
        return messageDao.getMessagesByChatId(chatId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun createChat(chat: Chat): String {
        chatDao.insertChat(chat.toEntity())
        return chat.id
    }

    override suspend fun updateChat(chat: Chat) {
        chatDao.updateChat(chat.toEntity())
    }

    override suspend fun deleteChat(chatId: String) {
        chatDao.deleteChatById(chatId)
    }

    override suspend fun archiveChat(chatId: String, isArchived: Boolean) {
        chatDao.updateChatArchiveStatus(chatId, isArchived)
    }

    override suspend fun pinChat(chatId: String, isPinned: Boolean) {
        chatDao.updateChatPinStatus(chatId, isPinned)
    }

    override suspend fun sendMessage(message: Message) {
        messageDao.insertMessage(message.toEntity())
        
        // Обновляем последнее сообщение в чате
        val preview = if (message.content.length > 100) {
            message.content.take(100) + "..."
        } else {
            message.content
        }
        chatDao.updateChatLastMessage(message.chatId, preview, Clock.System.now())
    }

    override suspend fun updateMessage(message: Message) {
        messageDao.updateMessage(message.toEntity())
    }

    override suspend fun deleteMessage(messageId: String) {
        messageDao.deleteMessageById(messageId)
    }

    override fun searchChats(query: String): Flow<List<Chat>> {
        return chatDao.searchChats(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchMessages(query: String): Flow<List<Message>> {
        return messageDao.searchAllMessages(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchMessagesInChat(chatId: String, query: String): Flow<List<Message>> {
        return messageDao.searchMessagesInChat(chatId, query).map { entities ->
            entities.map { it.toDomain() }
        }
    }
} 