package com.samaali.codememo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// COULEURS OFFICIELLES 2025 – CodeMemo Premium Look
private val CodeMemoLightColors = lightColorScheme(
    primary = Color(0xFF5856D6),        // Indigo violet magnifique
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDCD7FF),
    onPrimaryContainer = Color(0xFF1A1A3D),

    secondary = Color(0xFFFF375F),       // Corail rouge vif qui claque
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFD7DE),

    background = Color(0xFFF8F9FF),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE8E0FF),
    outline = Color(0xFF79747E),
    error = Color(0xFFB00020)
)

private val CodeMemoDarkColors = darkColorScheme(
    primary = Color(0xFFA19EFF),
    onPrimary = Color(0xFF1C1B4D),
    primaryContainer = Color(0xFF3F3D99),
    onPrimaryContainer = Color(0xFFE0DFFF),

    secondary = Color(0xFFFF375F),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFF5E001A),

    background = Color(0xFF12111A),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1E1D26),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF2A2833),
)

@Composable
fun CodeMemoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) CodeMemoDarkColors else CodeMemoLightColors,
        typography = CodeMemoTypography,
        shapes = AppShapes,
        content = content
    )
}