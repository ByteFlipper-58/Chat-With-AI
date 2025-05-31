package com.byteflipper.imageai.core.data.local.dao

import androidx.room.*
import com.byteflipper.imageai.core.data.local.entities.ChatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    
    @Query("SELECT * FROM chats WHERE isArchived = 0 ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllChats(): Flow<List<ChatEntity>>
    
    @Query("SELECT * FROM chats WHERE isArchived = 1 ORDER BY updatedAt DESC")
    fun getArchivedChats(): Flow<List<ChatEntity>>
    
    @Query("SELECT * FROM chats WHERE id = :chatId")
    suspend fun getChatById(chatId: String): ChatEntity?
    
    @Query("SELECT * FROM chats WHERE id = :chatId")
    fun getChatByIdFlow(chatId: String): Flow<ChatEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: ChatEntity)
    
    @Update
    suspend fun updateChat(chat: ChatEntity)
    
    @Delete
    suspend fun deleteChat(chat: ChatEntity)
    
    @Query("DELETE FROM chats WHERE id = :chatId")
    suspend fun deleteChatById(chatId: String)
    
    @Query("UPDATE chats SET isArchived = :isArchived WHERE id = :chatId")
    suspend fun updateChatArchiveStatus(chatId: String, isArchived: Boolean)
    
    @Query("UPDATE chats SET isPinned = :isPinned WHERE id = :chatId")
    suspend fun updateChatPinStatus(chatId: String, isPinned: Boolean)
    
    @Query("UPDATE chats SET lastMessagePreview = :preview, updatedAt = :timestamp, messageCount = messageCount + 1 WHERE id = :chatId")
    suspend fun updateChatLastMessage(chatId: String, preview: String, timestamp: kotlinx.datetime.Instant)
    
    @Query("SELECT * FROM chats WHERE title LIKE '%' || :query || '%' OR lastMessagePreview LIKE '%' || :query || '%'")
    fun searchChats(query: String): Flow<List<ChatEntity>>
} 