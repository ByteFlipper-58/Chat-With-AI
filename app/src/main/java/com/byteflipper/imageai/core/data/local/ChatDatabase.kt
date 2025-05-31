package com.byteflipper.imageai.core.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.byteflipper.imageai.core.data.local.converters.DateTimeConverters
import com.byteflipper.imageai.core.data.local.dao.ChatDao
import com.byteflipper.imageai.core.data.local.dao.MessageDao
import com.byteflipper.imageai.core.data.local.entities.ChatEntity
import com.byteflipper.imageai.core.data.local.entities.MessageEntity

@Database(
    entities = [ChatEntity::class, MessageEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(DateTimeConverters::class)
abstract class ChatDatabase : RoomDatabase() {
    
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
    
    companion object {
        const val DATABASE_NAME = "chat_database"
    }
} 