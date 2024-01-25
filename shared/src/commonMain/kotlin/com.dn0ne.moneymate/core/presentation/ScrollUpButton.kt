package com.dn0ne.moneymate.core.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.dn0ne.moneymate.MR
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun ScrollUpButton(
    visible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically { it / 2 } + fadeIn(),
        exit = slideOutVertically { it / 2 } + fadeOut(),
        label = "AnimatedVisibilityScrollUpButton",
        modifier = modifier
    ) {
        FilledTonalIconButton(
            onClick = onClick,
            modifier = Modifier
                .size(48.dp)
                .shadow(
                    elevation = 1.dp,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowUp,
                contentDescription = stringResource(MR.strings.scroll_up),
            )
        }
    }
}