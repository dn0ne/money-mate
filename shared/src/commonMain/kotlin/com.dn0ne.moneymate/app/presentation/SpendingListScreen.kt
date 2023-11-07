package com.dn0ne.moneymate.app.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dn0ne.moneymate.app.domain.Spending
import com.dn0ne.moneymate.app.presentation.components.AddSpendingSheet
import com.dn0ne.moneymate.app.presentation.components.SpendingListItem

@Composable
fun SpendingListScreen(
    state: SpendingListState,
    newSpending: Spending?,
    onEvent: (SpendingListEvent) -> Unit
) {
    val listState = rememberLazyListState()
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
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            state = listState
        ) {
            item {
                Text(text = "Spendings List", style = MaterialTheme.typography.displaySmall)
            }

            items(state.spendings) { spending ->
                SpendingListItem(
                    spending = spending,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEvent(SpendingListEvent.SelectSpending(spending)) }
                        .padding(horizontal = 16.dp)
                )
            }
        }
    }

    AddSpendingSheet(
        state = state,
        newSpending = newSpending,
        isOpen = state.isAddSpendingSheetOpen,
        onEvent = onEvent
    )
}