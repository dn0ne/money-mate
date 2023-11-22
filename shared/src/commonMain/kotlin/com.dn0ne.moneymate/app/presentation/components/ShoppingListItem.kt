package com.dn0ne.moneymate.app.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dn0ne.moneymate.app.domain.ShoppingItem

@Composable
fun ShoppingListItem(
    item: ShoppingItem,
    onItemNameChanged: (String) -> Unit,
    onItemPriceChanged: (Float) -> Unit,
    itemError: Pair<Boolean, Boolean>? = null,
    deletable: Boolean = true,
    onDeleteButtonClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        var name by remember {
            mutableStateOf(item.name)
        }
        SpendingTextField(
            value = name,
            onValueChanged = {
                name = it
                onItemNameChanged(name)
            },
            error = if (itemError?.first == true) "Fill this" else null,
            placeholder = "Name",
            keyboardType = KeyboardType.Text,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(16.dp))
        var price by remember {
            mutableStateOf(
                if (item.price == 0f) {
                    ""
                } else item.price.toString()
            )
        }
        SpendingTextField(
            value = price,
            maxLength = 6,
            onValueChanged = {
                price = it
                onItemPriceChanged(price.toFloatOrNull() ?: 0f)
            },
            placeholder = "Price",
            error = if (itemError?.second == true) "Fill this" else null,
            keyboardType = KeyboardType.Number,
            modifier = Modifier.weight(
                if (deletable) 0.7f else 1f
            )
        )
        if (deletable) {
            Column(modifier = Modifier.weight(.3f).fillMaxHeight()) {
                Spacer(modifier = Modifier.height(4.dp))
                IconButton(
                    onClick = onDeleteButtonClick,
                ) {
                    Icon(imageVector = Icons.Rounded.Close, contentDescription = "Delete item")
                }
            }
        }
    }
}