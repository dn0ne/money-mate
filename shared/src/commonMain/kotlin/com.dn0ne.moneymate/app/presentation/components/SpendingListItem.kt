package com.dn0ne.moneymate.app.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.dn0ne.moneymate.app.domain.entities.Category
import com.dn0ne.moneymate.app.domain.entities.Spending
import com.dn0ne.moneymate.app.domain.extensions.toStringWithScale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SpendingListItem(
    spending: Spending,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val category = spending.category ?: Category()
        CategoryIcon(category)

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (spending.shoppingList.isNotEmpty() || spending.shortDescription != null) {
                Text(
                    text = if (spending.shoppingList.isNotEmpty()) {
                        spending.shoppingList.take(3).joinToString(", ") { it.name } +
                                (if (spending.shoppingList.size > 3) "..." else "")
                    } else {
                        spending.shortDescription ?: ""
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth(.5f)
                        .basicMarquee()
                )

            }
        }


        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterEnd
        ) {
            val amount = spending.amount.toStringWithScale(2)

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = MaterialTheme.typography.titleLarge
                            .toSpanStyle()
                            .copy(color = MaterialTheme.colorScheme.primary)
                    ) {
                        append(amount.takeWhile { it != '.' })
                    }

                    withStyle(
                        style = MaterialTheme.typography.titleMedium
                            .toSpanStyle()
                            .copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    ) {
                        append(amount.dropWhile { it != '.' })
                    }
                }
            )
        }
    }
}