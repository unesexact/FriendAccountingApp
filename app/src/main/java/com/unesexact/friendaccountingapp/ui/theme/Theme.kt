package com.unesexact.friendaccountingapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Core Colors
val DarkBackground = Color(0xFF0F0F0F)
val CardBackground = Color(0xFF1A1A1A)
val AccentColor = Color(0xFF3A3A3A)
val PositiveBalance = Color(0xFF4CAF50)
val NegativeBalance = Color(0xFFE53935)
val NeutralBalance = Color(0xFFB0B0B0)

val WhiteText = Color.White

private val DarkColorScheme = darkColorScheme(
    background = DarkBackground,
    surface = CardBackground,
    primary = AccentColor,
    onPrimary = WhiteText,
    onBackground = WhiteText,
    onSurface = WhiteText
)

@Composable
fun FriendAccountingTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme, typography = Typography(), content = content
    )
}