package com.dn0ne.moneymate.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIos
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.dn0ne.moneymate.app.domain.Spending
import com.dn0ne.moneymate.app.extensions.formatToString
import com.dn0ne.moneymate.app.extensions.toLocalDate
import com.dn0ne.moneymate.app.extensions.toStringWithScale
import com.dn0ne.moneymate.app.presentation.CategoryIcons
import com.dn0ne.moneymate.app.presentation.SpendingListEvent
import com.dn0ne.moneymate.app.presentation.SpendingListState
import com.dn0ne.moneymate.core.presentation.SimpleBottomSheet

@Composable
fun SpendingDetailSheet(
    state: SpendingListState,
    selectedSpending: Spending?,
    isOpen: Boolean,
    onEvent: (SpendingListEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    SimpleBottomSheet(
        visible = isOpen,
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.primary)
    ) {
        val amount by remember {
            mutableStateOf(selectedSpending?.amount?.toStringWithScale(2))
        }
        CollapsingTopAppBar(
            isCollapsed = false,
            title = {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = MaterialTheme.typography.displayLarge
                                .toSpanStyle()
                                .copy(color = MaterialTheme.colorScheme.onPrimary)
                        ) {
                            append(amount?.takeWhile { it != '.' })
                        }

                        withStyle(
                            style = MaterialTheme.typography.displaySmall
                                .toSpanStyle()
                                .copy(color = MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            append(amount?.dropWhile { it != '.' })
                        }
                    }
                )
            },
            collapsedTitle = {},
            leadingButton = {
                IconButton(
                    onClick = { onEvent(SpendingListEvent.DismissSpending) },
                    colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onPrimary)
                ) {
                    Icon(imageVector = Icons.Rounded.ArrowBackIos, contentDescription = "Back")
                }
            }
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .clip(shape = RoundedCornerShape(16.dp))
                .background(color = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                    .padding(16.dp)
            ) {
                val spentAt by remember {
                    mutableStateOf(
                        selectedSpending?.spentAt?.toLocalDate()
                    )
                }
                Text(
                    text = spentAt?.formatToString() ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(16.dp))
                Icon(
                    imageVector = CategoryIcons.getIconByName(
                        selectedSpending?.category?.iconName ?: ""
                    ),
                    contentDescription = "Category icon",
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = "${selectedSpending?.category?.name}",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (selectedSpending?.shortDescription != null) {
                Text(
                    text = selectedSpending.shortDescription!!,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            if (selectedSpending?.shoppingList?.isNotEmpty() == true) {
                for (item in selectedSpending.shoppingList) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    ) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        val price by remember {
                            mutableStateOf(item.price.toStringWithScale(2))
                        }
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = MaterialTheme.typography.titleLarge
                                        .toSpanStyle()
                                        .copy(color = MaterialTheme.colorScheme.primary)
                                ) {
                                    append(price.takeWhile { it != '.' })
                                }

                                withStyle(
                                    style = MaterialTheme.typography.titleMedium
                                        .toSpanStyle()
                                        .copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                ) {
                                    append(price.dropWhile { it != '.' })
                                }
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.weight(.5f))

                OutlinedButton(
                    onClick = { onEvent(SpendingListEvent.DeleteSpending) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Delete spending",
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Delete")
                }
                Spacer(modifier = Modifier.width(16.dp))
                FilledTonalButton(
                    onClick = {
                        selectedSpending?.let {
                            onEvent(SpendingListEvent.EditSpending(it))
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "Edit spending",
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Edit")
                }
                Spacer(modifier = Modifier.weight(.5f))

            }
        }
    }
}