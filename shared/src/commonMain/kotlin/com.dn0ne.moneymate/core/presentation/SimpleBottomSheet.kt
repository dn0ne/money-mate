package com.dn0ne.moneymate.core.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dn0ne.moneymate.app.extensions.safeSystemBarsAndDisplayCutoutPadding

@Composable
fun SimpleBottomSheet(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            animationSpec = tween(300),
            initialOffsetY = { it }
        ),
        exit = slideOutVertically(
            animationSpec = tween(300),
            targetOffsetY = { it }
        ),
        modifier = Modifier.safeSystemBarsAndDisplayCutoutPadding()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.clickable(enabled = false, onClick = {})
        ) {
            content()
        }
    }
}