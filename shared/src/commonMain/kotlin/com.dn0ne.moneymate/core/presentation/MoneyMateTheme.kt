package com.dn0ne.moneymate.core.presentation

import androidx.compose.runtime.Composable

@Composable
expect fun MoneyMateTheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    content: @Composable () -> Unit
)