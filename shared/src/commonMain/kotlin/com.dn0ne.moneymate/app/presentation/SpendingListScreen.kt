package com.dn0ne.moneymate.app.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.dn0ne.moneymate.app.domain.Spending
import com.dn0ne.moneymate.app.extensions.formatToString
import com.dn0ne.moneymate.app.extensions.toLocalDate
import com.dn0ne.moneymate.app.extensions.toStringWithScale
import com.dn0ne.moneymate.app.presentation.components.AddSpendingSheet
import com.dn0ne.moneymate.app.presentation.components.CollapsingTopAppBar
import com.dn0ne.moneymate.app.presentation.components.SpendingDetailSheet
import com.dn0ne.moneymate.app.presentation.components.SpendingListItem
import kotlinx.coroutines.launch

@Composable
fun SpendingListScreen(
    state: SpendingListState,
    newSpending: Spending?,
    onEvent: (SpendingListEvent) -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = "Spend") },
                icon = { Icon(imageVector = Icons.Rounded.Payments, contentDescription = null) },
                onClick = { onEvent(SpendingListEvent.OnAddNewSpendingClick) },
                expanded = !listState.canScrollBackward,
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        }
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.primary,
                )
        ) {

            val totalAmount = state.spendings.map { it.amount }.sum().toStringWithScale(2)
            CollapsingTopAppBar(
                isCollapsed = false,
                title = {
                    Text(
                        text = "Total spending",
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
                                append(totalAmount.takeWhile { it != '.' })
                            }

                            withStyle(
                                style = MaterialTheme.typography.displaySmall
                                    .toSpanStyle()
                                    .copy(color = MaterialTheme.colorScheme.secondaryContainer)
                            ) {
                                append(totalAmount.dropWhile { it != '.' })
                            }
                        }
                    )
                },
                collapsedTitle = {}
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape = RoundedCornerShape(16.dp, 16.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {

                DragHandle()

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .onSizeChanged {
                            coroutineScope.launch {
                                listState.animateScrollToItem(0)
                            }
                        },
                    state = listState,
                ) {
                    if (state.spendings.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Text(
                                    text = "There's nothing here yet...",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .align(
                                            Alignment.Center
                                        )
                                        .padding(8.dp)
                                )
                            }
                        }
                    }


                    itemsIndexed(
                        state.spendings,
                        key = { _, item -> item.id.toString() }
                    ) { index, spending ->
                        spending.spentAt.toLocalDate().let {
                            if (it != state.spendings.getOrNull(index - 1)?.spentAt?.toLocalDate()) {
                                Text(
                                    text = it.formatToString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }

                        SpendingListItem(
                            spending = spending,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onEvent(SpendingListEvent.SelectSpending(spending)) }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }

    AddSpendingSheet(
        state = state,
        newSpending = newSpending,
        isOpen = state.isAddSpendingSheetOpen,
        onEvent = onEvent
    )

    SpendingDetailSheet(
        state = state,
        selectedSpending = state.selectedSpending,
        isOpen = state.isSelectedSpendingSheetOpen,
        onEvent = onEvent
    )
}

@Composable
private fun DragHandle(modifier: Modifier = Modifier) {
    Column(modifier) {
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(
                modifier = Modifier
                    .width(32.dp)
                    .height(4.dp)
                    .clip(shape = RoundedCornerShape(100))
                    .background(color = MaterialTheme.colorScheme.outlineVariant)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}