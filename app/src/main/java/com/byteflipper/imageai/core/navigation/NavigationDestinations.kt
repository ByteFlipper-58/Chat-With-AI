package com.byteflipper.imageai.core.navigation

sealed class NavigationDestinations(val route: String) {
    object ChatList : NavigationDestinations("chat_list")
    object ChatDetail : NavigationDestinations("chat_detail/{chatId}") {
        fun createRoute(chatId: String) = "chat_detail/$chatId"
    }
    object Settings : NavigationDestinations("settings")
    object Search : NavigationDestinations("search")
    object ArchivedChats : NavigationDestinations("archived_chats")
    
    companion object {
        const val CHAT_ID_KEY = "chatId"
    }
} 