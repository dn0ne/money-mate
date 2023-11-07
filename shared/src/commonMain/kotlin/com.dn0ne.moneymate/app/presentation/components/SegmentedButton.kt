package com.dn0ne.moneymate.app.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun SegmentedButton(
    items: List<String>,
    modifier: Modifier = Modifier,
    defaultSelectedItemIndex: Int = 0,
    onItemSelection: (selectedItemIndex: Int) -> Unit
) {
    var selectedIndex by remember { mutableStateOf(defaultSelectedItemIndex) }

    Row(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(100))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(100)
            )
    ) {
        items.forEachIndexed { index, item ->
            OutlinedButton(
                contentPadding = PaddingValues(horizontal = 4.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                onClick = {
                    selectedIndex = index
                    onItemSelection(selectedIndex)
                },
                shape = RoundedCornerShape(0),
                border = null,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = animateColorAsState(
                        targetValue = if (selectedIndex == index) {
                            MaterialTheme.colorScheme.secondaryContainer
                        } else MaterialTheme.colorScheme.surface,
                        animationSpec = tween(),
                        label = "containerColorAnimation"
                    ).value,
                    contentColor = animateColorAsState(
                        targetValue = if (selectedIndex == index) {
                            MaterialTheme.colorScheme.onSecondaryContainer
                        } else MaterialTheme.colorScheme.onSurface,
                        animationSpec = tween(),
                        label = "contentColorAnimation"
                    ).value
                )
            ) {
                AnimatedVisibility(
                    visible = selectedIndex == index
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = "Selected",
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = item
                )
            }
            if (index != items.lastIndex) {
                Divider(
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                )
            }
        }
    }
}