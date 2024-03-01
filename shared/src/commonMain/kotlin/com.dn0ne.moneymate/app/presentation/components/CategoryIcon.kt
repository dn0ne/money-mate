package com.dn0ne.moneymate.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dn0ne.moneymate.app.domain.entities.Category
import com.dn0ne.moneymate.app.presentation.CategoryIcons

@Composable
fun CategoryIcon(
    category: Category,
    iconSize: Dp = 24.dp,
    modifier: Modifier = Modifier.size(48.dp)
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(100))
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = CategoryIcons.getIconByName(category.iconName),
            contentDescription = category.iconName + " icon",
            modifier = Modifier.size(iconSize),
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}