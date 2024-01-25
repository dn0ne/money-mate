package com.dn0ne.moneymate.app.extensions

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Modifier.safeSystemBarsAndDisplayCutoutPadding() =
    windowInsetsPadding(WindowInsets.systemBars)
        .windowInsetsPadding(WindowInsets.displayCutout)