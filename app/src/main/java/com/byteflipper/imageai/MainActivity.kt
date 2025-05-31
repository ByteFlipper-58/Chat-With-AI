package com.byteflipper.imageai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.byteflipper.imageai.R
import com.byteflipper.imageai.core.navigation.NavigationDestinations
import com.byteflipper.imageai.core.theme.ImageAITheme
import com.byteflipper.imageai.feature_chat.presentation.chat_list.ChatListScreen
import com.byteflipper.imageai.feature_chat.presentation.chat_detail.ChatDetailScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ImageAITheme {
                ChatApp()
            }
        }
    }
}

@Composable
fun ChatApp() {
    val navController = rememberNavController()
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        ChatNavigation(navController = navController)
    }
}

@Composable
fun ChatNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavigationDestinations.ChatDetail.createRoute("new")
    ) {
        // Экран списка чатов (история)
        composable(NavigationDestinations.ChatList.route) {
            ChatListScreen(
                onChatClick = { chatId ->
                    navController.navigate(
                        NavigationDestinations.ChatDetail.createRoute(chatId)
                    ) {
                        // Очищаем стек навигации при переходе к конкретному чату
                        popUpTo(NavigationDestinations.ChatList.route) { inclusive = false }
                    }
                },
                onNewChatClick = {
                    navController.navigate(
                        NavigationDestinations.ChatDetail.createRoute("new")
                    ) {
                        popUpTo(NavigationDestinations.ChatList.route) { inclusive = false }
                    }
                },
                onSettingsClick = {
                    navController.navigate(NavigationDestinations.Settings.route)
                }
            )
        }
        
        // Экран деталей чата (теперь стартовый)
        composable(
            route = NavigationDestinations.ChatDetail.route,
            arguments = listOf(navArgument(NavigationDestinations.CHAT_ID_KEY) { 
                type = NavType.StringType 
            })
        ) {
            ChatDetailScreen(
                onBackClick = {
                    // При нажатии назад из чата идем к списку чатов
                    navController.navigate(NavigationDestinations.ChatList.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                onHistoryClick = {
                    // Переход к истории чатов
                    navController.navigate(NavigationDestinations.ChatList.route)
                }
            )
        }
        
        // Экран настроек (заглушка)
        composable(
            route = NavigationDestinations.Settings.route
        ) {
            SettingsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        // Экран поиска (заглушка)
        composable(
            route = NavigationDestinations.Search.route
        ) {
            // TODO: Реализовать экран поиска
        }
        
        // Экран архивированных чатов (заглушка)
        composable(
            route = NavigationDestinations.ArchivedChats.route
        ) {
            // TODO: Реализовать экран архивированных чатов
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_back_24px),
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        // TODO: Добавить настройки приложения
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text(
                text = "Настройки будут добавлены в следующих версиях",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
