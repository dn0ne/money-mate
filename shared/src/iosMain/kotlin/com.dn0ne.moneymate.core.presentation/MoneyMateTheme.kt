package com.dn0ne.moneymate.core.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.dn0ne.moneymate.app.ui.theme.DarkColorScheme
import com.dn0ne.moneymate.app.ui.theme.LightColorScheme

@Composable
actual fun MoneyMateTheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        content = content
    )
}