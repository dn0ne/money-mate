package com.dn0ne.moneymate.app.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dn0ne.moneymate.MR
import com.dn0ne.moneymate.app.domain.ShoppingItem
import com.dn0ne.moneymate.util.DecimalFormatter
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalMaterial3Api::class)
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
            error = if (itemError?.first == true) stringResource(MR.strings.item_error) else null,
            placeholder = stringResource(MR.strings.name),
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
                price = DecimalFormatter.cleanup(it)
                onItemPriceChanged(price.toFloatOrNull() ?: 0f)
            },
            placeholder = stringResource(MR.strings.price),
            error = if (itemError?.second == true) stringResource(MR.strings.item_error) else null,
            keyboardType = KeyboardType.Number,
            modifier = Modifier.weight(
                if (deletable) 0.7f else 1f
            )
        )
        if (deletable) {
            Column(modifier = Modifier.weight(.3f).fillMaxHeight()) {
                Spacer(modifier = Modifier.height(4.dp))
                PlainTooltipBox(
                    tooltip = {
                        Text(text = stringResource(MR.strings.remove_item_tooltip))
                    }
                ) {
                    IconButton(
                        onClick = onDeleteButtonClick,
                        modifier = Modifier.tooltipAnchor()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = stringResource(MR.strings.delete_item_button_description)
                        )
                    }
                }
            }
        }
    }
}