package com.dn0ne.moneymate.app.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DotPageIndicator(
    pageCount: Int,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    selectedDotSize: Dp = 12.dp,
    dotSize: Dp = 10.dp,
    selectedDotColor: Color = MaterialTheme.colorScheme.primary,
    dotColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .size(
                        animateDpAsState(
                            targetValue = if (index == selectedIndex) selectedDotSize else dotSize
                        ).value
                    )
                    .padding(2.dp)
                    .clip(RoundedCornerShape(100))
                    .background(
                        color = animateColorAsState(
                            targetValue = if (index == selectedIndex) selectedDotColor else dotColor
                        ).value
                    )
            )
        }
    }
}