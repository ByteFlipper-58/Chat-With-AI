package com.byteflipper.imageai.core.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryVariant,
    onPrimaryContainer = OnPrimary,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryVariant,
    onSecondaryContainer = OnSecondary,
    tertiary = Info,
    onTertiary = OnPrimary,
    error = Error,
    onError = OnPrimary,
    errorContainer = Error,
    onErrorContainer = OnPrimary,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceDark,
    onSurfaceVariant = OnSurfaceDark,
    outline = MessageBorderDark,
    outlineVariant = MessageBorderDark,
    scrim = Color.Black.copy(alpha = 0.32f),
    inverseSurface = Surface,
    inverseOnSurface = OnSurface,
    inversePrimary = Primary,
    surfaceDim = SurfaceDark,
    surfaceBright = Color(0xFF2A2A3E),
    surfaceContainerLowest = BackgroundDark,
    surfaceContainerLow = Color(0xFF16162A),
    surfaceContainer = Color(0xFF1A1A2E),
    surfaceContainerHigh = Color(0xFF1E1E32),
    surfaceContainerHighest = Color(0xFF222236)
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = Color(0xFF1E1B3C),
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = Color(0xFFCFFAFE),
    onSecondaryContainer = Color(0xFF0E3A3F),
    tertiary = Info,
    onTertiary = OnPrimary,
    error = Error,
    onError = OnPrimary,
    errorContainer = Color(0xFFFFEDED),
    onErrorContainer = Color(0xFF5F2120),
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = Color(0xFFF8FAFC),
    onSurfaceVariant = Color(0xFF64748B),
    outline = MessageBorder,
    outlineVariant = Color(0xFFE2E8F0),
    scrim = Color.Black.copy(alpha = 0.32f),
    inverseSurface = Color(0xFF313033),
    inverseOnSurface = Color(0xFFF4F0F4),
    inversePrimary = Color(0xFFBDB9FF),
    surfaceDim = Color(0xFFDDD8E1),
    surfaceBright = Surface,
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF7F2FB),
    surfaceContainer = Color(0xFFF1ECF5),
    surfaceContainerHigh = Color(0xFFEBE6EF),
    surfaceContainerHighest = Color(0xFFE6E0E9)
)

@Composable
fun ImageAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Отключаем dynamic color для использования кастомной темы
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val systemUiController = rememberSystemUiController()
    
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = colorScheme.background,
            darkIcons = !darkTheme
        )
        systemUiController.setNavigationBarColor(
            color = colorScheme.surface,
            darkIcons = !darkTheme
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}