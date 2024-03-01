package com.dn0ne.moneymate.app.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dn0ne.moneymate.app.domain.entities.Category
import com.dn0ne.moneymate.app.presentation.CategoryIcons

@Composable
fun IconPicker(
    newCategory: Category?,
    onPick: (iconName: String) -> Unit,
    onModeChange: (isInPickMode: Boolean) -> Unit = {},
    error: String? = null,
    modifier: Modifier = Modifier
) {
    var isInPickMode by remember {
        mutableStateOf(false)
    }
    var pickedIconName by remember {
        mutableStateOf(newCategory?.iconName ?: "")
    }
    AnimatedContent(
        targetState = isInPickMode,
        modifier = modifier
            .heightIn(max = 300.dp)
            .animateContentSize()
    ) { state ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {


            when (state) {
                false -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(100))
                            .border(
                                width = 1.dp,
                                color = error?.let { MaterialTheme.colorScheme.error }
                                    ?: MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(100)
                            )
                            .clickable {
                                isInPickMode = true
                                onModeChange(isInPickMode)
                            }
                    ) {
                        Icon(
                            imageVector =
                            if (pickedIconName.isBlank()) Icons.Rounded.Add
                            else CategoryIcons.getIconByName(pickedIconName),
                            contentDescription = "Category icon",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    error?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                }

                true -> {
                    CategoryIcons.names.chunked(4).forEach { chunk ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 28.dp, vertical = 8.dp)
                        ) {
                            chunk.forEach { iconName ->
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(100))
                                        .background(
                                            color = if (iconName == pickedIconName) {
                                                MaterialTheme.colorScheme.surfaceVariant
                                            } else {
                                                Color.Transparent
                                            }
                                        )
                                        .clickable {
                                            onPick(iconName)
                                            pickedIconName = iconName
                                            isInPickMode = false
                                            onModeChange(isInPickMode)
                                        }
                                ) {
                                    Icon(
                                        imageVector = CategoryIcons.getIconByName(iconName),
                                        contentDescription = "$iconName icon",
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}