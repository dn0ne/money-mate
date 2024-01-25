package com.dn0ne.moneymate.core.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
actual fun BackGestureHandler(onBack: () -> Unit) {
    BackHandler(onBack = onBack)
}
