package com.dn0ne.moneymate.core.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.dn0ne.moneymate.app.theme.DarkColorScheme
import com.dn0ne.moneymate.app.theme.LightColorScheme
import com.dn0ne.moneymate.app.theme.Typography

@Composable
actual fun MoneyMateTheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}