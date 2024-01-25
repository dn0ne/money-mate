package com.dn0ne.moneymate.app.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dn0ne.moneymate.MR
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun ShoppingListTitles(modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Text(
            text = stringResource(MR.strings.item_name),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(MR.strings.price),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}