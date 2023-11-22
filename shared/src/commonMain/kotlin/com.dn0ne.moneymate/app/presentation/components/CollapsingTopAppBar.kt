package com.dn0ne.moneymate.app.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CollapsingTopAppBar(
    isCollapsed: Boolean,
    title: @Composable () -> Unit,
    collapsedTitle: @Composable () -> Unit,
    leadingButton: @Composable () -> Unit,
    trailingButtons: (@Composable RowScope.() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val minTopBarHeight = 48.dp
    Box(
        modifier = modifier
            .animateContentSize()
            .heightIn(min = minTopBarHeight)
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Box(modifier = Modifier.align(Alignment.BottomStart)) {
            leadingButton()
        }

        AnimatedVisibility(
            visible = isCollapsed,
            enter = fadeIn() + expandVertically(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = EaseInOut
                )
            ),
            exit = fadeOut() + shrinkVertically(
                animationSpec = tween(
                    durationMillis = 500,
                    easing = EaseInOut
                )
            ),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.height(minTopBarHeight)
            ) {
                collapsedTitle()
            }
        }

        AnimatedVisibility(
            visible = !isCollapsed,
            enter = fadeIn() + expandVertically(
                animationSpec = tween(
                    durationMillis = 500,
                    easing = EaseInOut
                )
            ),
            exit = fadeOut() + shrinkVertically(
                animationSpec = tween(
                    durationMillis = 500,
                    easing = EaseInOut
                )
            ),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxHeight(.3f)
            ) {
                title()
            }
        }

        if (trailingButtons != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                trailingButtons()
            }
        }
    }
}