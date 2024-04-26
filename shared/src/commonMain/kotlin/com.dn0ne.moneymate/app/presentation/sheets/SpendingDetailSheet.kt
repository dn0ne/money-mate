package com.dn0ne.moneymate.app.presentation.sheets

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIos
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dn0ne.moneymate.MR
import com.dn0ne.moneymate.app.domain.entities.spending.Spending
import com.dn0ne.moneymate.app.domain.extensions.toLocalDate
import com.dn0ne.moneymate.app.domain.extensions.toStringWithScale
import com.dn0ne.moneymate.app.presentation.CategoryIcons
import com.dn0ne.moneymate.app.presentation.SpendingListEvent
import com.dn0ne.moneymate.core.presentation.SimpleBottomSheet
import com.dn0ne.moneymate.app.domain.util.DateFormatter
import com.dn0ne.moneymate.app.presentation.components.CollapsingTopAppBar
import dev.icerock.moko.resources.compose.stringResource
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SpendingDetailSheet(
    selectedSpending: Spending?,
    isOpen: Boolean,
    onEvent: (SpendingListEvent) -> Unit,
    modifier: Modifier = Modifier,
    maxContentWidth: Dp = Dp.Unspecified
) {
    SimpleBottomSheet(
        visible = isOpen,
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface)
    ) {
        val amount by remember {
            mutableStateOf(selectedSpending?.amount?.toStringWithScale(2))
        }

        var isTopBarCollapsed by remember {
            mutableStateOf(false)
        }
        val scrollState = rememberScrollState()
        var isScrollingUp by remember {
            mutableStateOf(true)
        }
        val scrollableState = rememberScrollableState {
            if (it.roundToInt() > 5) {
                isScrollingUp = true
            } else if (it.roundToInt() < -5 || scrollState.canScrollBackward) {
                isScrollingUp = false
            }
            it
        }

        if (!scrollState.canScrollBackward && isScrollingUp) {
            isTopBarCollapsed = false
        } else if (!isScrollingUp) {
            isTopBarCollapsed = true
        }

        CollapsingTopAppBar(
            isCollapsed = isTopBarCollapsed,
            title = {
                amount?.let {
                    Text(
                        text = stringResource(MR.strings.total),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = MaterialTheme.typography.displayLarge
                                    .toSpanStyle()
                                    .copy(color = MaterialTheme.colorScheme.onSurface)
                            ) {
                                append(it.takeWhile { it != '.' })
                            }

                            withStyle(
                                style = MaterialTheme.typography.displayMedium
                                    .toSpanStyle()
                                    .copy(color = MaterialTheme.colorScheme.primary)
                            ) {
                                append(it.dropWhile { it != '.' })
                            }
                        }
                    )
                }
            },
            collapsedTitle = {
                Text(
                    "${stringResource(MR.strings.total)} $amount",
                    style = MaterialTheme.typography.titleMedium
                )
            },
            leadingButton = {
                IconButton(
                    onClick = {
                        onEvent(SpendingListEvent.DismissSpending)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBackIos,
                        contentDescription = stringResource(MR.strings.spending_detail_sheet_close_description),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            modifier = Modifier.widthIn(max = maxContentWidth)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .widthIn(max = maxContentWidth)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(28.dp))
                .scrollable(scrollableState, Orientation.Vertical)
                .verticalScroll(scrollState)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(28.dp))
                    .background(color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
            ) {
                val spentAt by remember {
                    mutableStateOf(
                        selectedSpending?.spentAt?.toLocalDate()
                    )
                }
                Text(
                    text = spentAt?.let { DateFormatter.formatToDayOfWeekDayMonth(it) } ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(16.dp)
                )

                Icon(
                    imageVector = CategoryIcons.getIconByName(
                        selectedSpending?.category?.iconName ?: ""
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = "${selectedSpending?.shortDescription ?: selectedSpending?.category?.name}",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                        .basicMarquee()
                )

                selectedSpending?.shoppingList?.forEach { item ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            modifier = Modifier
                                .fillMaxWidth(.6f)
                                .basicMarquee()
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
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
            ) {
                OutlinedButton(
                    onClick = { onEvent(SpendingListEvent.DeleteSpending) },
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(MR.strings.delete),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                FilledTonalButton(
                    onClick = {
                        selectedSpending?.let {
                            onEvent(SpendingListEvent.EditSpending(it))
                        }
                    },
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(MR.strings.edit),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}