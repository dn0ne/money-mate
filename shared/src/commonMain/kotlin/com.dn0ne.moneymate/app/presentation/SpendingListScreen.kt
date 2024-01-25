package com.dn0ne.moneymate.app.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.PieChart
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dn0ne.moneymate.MR
import com.dn0ne.moneymate.app.domain.Category
import com.dn0ne.moneymate.app.domain.Spending
import com.dn0ne.moneymate.app.extensions.safeSystemBarsAndDisplayCutoutPadding
import com.dn0ne.moneymate.app.extensions.toLocalDate
import com.dn0ne.moneymate.app.extensions.toStringWithScale
import com.dn0ne.moneymate.app.extensions.today
import com.dn0ne.moneymate.app.presentation.components.AddSpendingSheet
import com.dn0ne.moneymate.app.presentation.components.DotPageIndicator
import com.dn0ne.moneymate.app.presentation.components.SettingsSheet
import com.dn0ne.moneymate.app.presentation.components.SpendingDetailSheet
import com.dn0ne.moneymate.app.presentation.components.SpendingListItem
import com.dn0ne.moneymate.app.presentation.components.SummarySheet
import com.dn0ne.moneymate.core.presentation.BackGestureHandler
import com.dn0ne.moneymate.core.presentation.BannerAd
import com.dn0ne.moneymate.core.presentation.ScrollUpButton
import com.dn0ne.moneymate.core.presentation.animateTextStyleAsState
import com.dn0ne.moneymate.util.DateFormatter
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SpendingListScreen(
    state: SpendingListState,
    newSpending: Spending?,
    newCategory: Category?,
    onEvent: (SpendingListEvent) -> Unit
) {
    BackGestureHandler {
        onEvent(SpendingListEvent.DismissSpending)
        if (!state.isSelectedSpendingSheetOpen && !state.isAddSpendingSheetOpen) {
            onEvent(SpendingListEvent.OnSummaryBackClick)
        }
    }

    val maxWidth by remember {
        mutableStateOf(700.dp)
    }
    var adHeight by remember {
        mutableStateOf(0.dp)
    }
    val fabOffsetY = animateDpAsState(
        targetValue = adHeight
    )

    val localDensity = LocalDensity.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        floatingActionButton = {
            Box(
                modifier = Modifier.fillMaxWidth().offset(y = -fabOffsetY.value)
            ) {
                ExtendedFloatingActionButton(
                    text = { Text(text = stringResource(MR.strings.spend)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Payments,
                            contentDescription = null
                        )
                    },
                    onClick = { onEvent(SpendingListEvent.OnAddNewSpendingClick) },
                    expanded = !listState.canScrollBackward,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )

                ScrollUpButton(
                    visible = listState.firstVisibleItemIndex > 5,
                    onClick = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(0)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(start = 32.dp)
                )
            }
        },
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.surface)
        ) {
            var screenWidth by remember {
                mutableStateOf(0.dp)
            }
            var screenHeight by remember {
                mutableStateOf(0.dp)
            }

            Box(
                modifier = Modifier
                    .widthIn(max = maxWidth)
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                    )
                    .onGloballyPositioned {
                        with(localDensity) {
                            screenWidth = it.size.width.toDp()
                            screenHeight = it.size.height.toDp()
                        }
                    }
                    .safeSystemBarsAndDisplayCutoutPadding()
            ) {
                var topPanelHeight by remember {
                    mutableStateOf(0.dp)
                }
                val panelButtonsHeight by remember {
                    mutableStateOf(72.dp + 16.dp * 2)
                }
                val listBackground by mutableStateOf(
                    MaterialTheme.colorScheme.surfaceColorAtElevation(
                        1.dp
                    )
                )

                val filteredSpendings = if (state.settings.budgetAmount > 0f) {
                    state.spendings
                        .filter {
                            it.spentAt.toLocalDate() in state.settings.periodStartDate..LocalDate.today()
                        }
                } else state.spendings

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Spacer(modifier = Modifier.height(panelButtonsHeight))

                    if (state.spendings.isEmpty()) {
                        Spacer(modifier = Modifier.height(topPanelHeight - panelButtonsHeight))
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(28.dp, 28.dp))
                                .background(listBackground)
                        ) {
                            Text(
                                text = stringResource(MR.strings.empty_list_greeting),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(50.dp)
                            )
                        }
                    } else if (filteredSpendings.isEmpty()) {
                        Spacer(modifier = Modifier.height(topPanelHeight - panelButtonsHeight))
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(28.dp, 28.dp))
                                .background(listBackground)
                        ) {
                            Text(
                                text = stringResource(MR.strings.empty_filtered_list_greeting),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(50.dp)
                            )
                        }
                    } else {
                        LaunchedEffect(filteredSpendings.size) {
                            listState.animateScrollToItem(0)
                        }
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(28.dp, 28.dp)),
                            state = listState,
                        ) {
                            item {
                                Spacer(modifier = Modifier.height(topPanelHeight - panelButtonsHeight))
                            }

                            item {
                                DragHandle(
                                    modifier = Modifier
                                        .clip(
                                            RoundedCornerShape(
                                                topStart = 28.dp,
                                                topEnd = 28.dp
                                            )
                                        )
                                        .background(listBackground)
                                )
                            }

                            filteredSpendings
                                .groupBy { it.spentAt.toLocalDate() }
                                .forEach { (date, spendings) ->
                                    stickyHeader(key = date.toString()) {
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(color = listBackground)
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(
                                                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                                        3.dp
                                                    )
                                                )
                                                .padding(16.dp)
                                        ) {
                                            Text(
                                                text = DateFormatter.formatToDayOfWeekDayMonth(date),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )

                                            Text(
                                                text = buildAnnotatedString {
                                                    val amount = spendings.map { it.amount }.sum().toStringWithScale(2)
                                                    withStyle(
                                                        style = MaterialTheme.typography.bodyMedium
                                                            .toSpanStyle()
                                                            .copy(color = MaterialTheme.colorScheme.primary)
                                                    ) {
                                                        append(amount.takeWhile { it != '.' })
                                                    }

                                                    withStyle(
                                                        style = MaterialTheme.typography.bodySmall
                                                            .toSpanStyle()
                                                            .copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    ) {
                                                        append(amount.dropWhile { it != '.' })
                                                    }
                                                }
                                            )
                                        }
                                    }

                                    items(
                                        items = spendings,
                                        key = { it.id.toHexString() }
                                    ) { spending ->
                                        SpendingListItem(
                                            spending = spending,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(color = listBackground)
                                                .clickable {
                                                    onEvent(
                                                        SpendingListEvent.SelectSpending(
                                                            spending
                                                        )
                                                    )
                                                }
                                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                        )
                                    }
                                }

                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .requiredHeight(800.dp)
                                        .background(color = listBackground)
                                )
                            }
                        }
                    }
                }

                BannerAd(
                    adId = stringResource(MR.strings.ad),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))
                        .align(Alignment.BottomCenter)
                        .animateContentSize()
                        .onGloballyPositioned {
                            adHeight = with(localDensity) {
                                it.size.height.toDp()
                            }
                        }
                )

                val totalAmount = filteredSpendings.map { it.amount }.sum()
                val remainingAmount = state.settings.budgetAmount - totalAmount

                val totalAmountString = totalAmount.toStringWithScale(2)
                val remainingAmountString = remainingAmount.toStringWithScale(2)


                TopPanel(
                    state = state,
                    totalAmount = totalAmountString,
                    remainingAmount = remainingAmountString,
                    listState = listState,
                    onSummaryClick = {
                        onEvent(SpendingListEvent.OnSummaryClick)
                    },
                    onSettingsClick = {
                        onEvent(SpendingListEvent.OnSettingsClick)
                    },
                    wideLayout = screenWidth > screenHeight,
                    modifier = Modifier
                        .onGloballyPositioned {
                            topPanelHeight = localDensity.run { it.size.height.toDp() }
                        }
                )


            }
        }
    }


    SettingsSheet(
        state = state,
        newCategory = newCategory,
        isOpen = state.isSettingsSheetOpen,
        onEvent = onEvent,
        maxContentWidth = maxWidth,
    )

    SummarySheet(
        state = state,
        isOpen = state.isSummarySheetOpen,
        onEvent = onEvent,
        maxContentWidth = maxWidth,
    )

    AddSpendingSheet(
        state = state,
        newSpending = newSpending,
        newCategory = newCategory,
        isOpen = state.isAddSpendingSheetOpen,
        onEvent = onEvent,
        maxContentWidth = maxWidth,
    )

    SpendingDetailSheet(
        selectedSpending = state.selectedSpending,
        isOpen = state.isSelectedSpendingSheetOpen,
        onEvent = onEvent,
        maxContentWidth = maxWidth,
    )
}


@Composable
private fun DragHandle(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
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
                    .background(color = MaterialTheme.colorScheme.surfaceColorAtElevation(50.dp))
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TopPanel(
    state: SpendingListState,
    totalAmount: String,
    remainingAmount: String,
    listState: LazyListState,
    wideLayout: Boolean,
    modifier: Modifier = Modifier,
    onSummaryClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = if (wideLayout) 540.dp else Dp.Unspecified)
            .fillMaxHeight(if (wideLayout) .75f else .35f)
            .padding(16.dp)
    ) {
        val localDensity = LocalDensity.current

        val maxOffsetY by remember {
            mutableStateOf(87.dp)
        }
        var offsetY by remember {
            mutableStateOf(0.dp)
        }
        offsetY = if (listState.firstVisibleItemIndex == 0) {
            with(localDensity) { listState.firstVisibleItemScrollOffset.toDp() }
        } else {
            87.dp * 2
        }

        AnimatedVisibility(
            visible = offsetY < maxOffsetY / 2,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
            label = "AnimatedVisibilityTopButtons",
        ) {
            Column {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                ) {
                    FilledTonalButton(
                        onClick = onSummaryClick,
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PieChart,
                            contentDescription = stringResource(MR.strings.summary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        val text = stringResource(MR.strings.summary)
                        Text(
                            text = text,
                            style = if (text.length > 8) {
                                MaterialTheme.typography.titleSmall
                            } else MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            modifier = Modifier.basicMarquee()
                        )
                    }
                    FilledTonalButton(
                        onClick = onSettingsClick,
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = stringResource(MR.strings.settings)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        val text = stringResource(MR.strings.settings)
                        Text(
                            text = text,
                            style = if (text.length > 8) {
                                MaterialTheme.typography.titleSmall
                            } else MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            modifier = Modifier.basicMarquee()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        val maxHeightFraction =
            if (listState.firstVisibleItemIndex == 0) {
                1f - (offsetY.value) / 2.9f / maxOffsetY.value
            } else 0f

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeightIn(72.dp)
                .fillMaxHeight(maxHeightFraction)
                .clip(RoundedCornerShape(28.dp))
                .background(color = MaterialTheme.colorScheme.primary)
        ) {
            val isExpanded = offsetY * .7f < maxOffsetY

            val bigNumbersStyle by animateTextStyleAsState(
                targetValue = if (isExpanded) {
                    MaterialTheme.typography.displayLarge
                } else MaterialTheme.typography.displaySmall,
            )
            val littleNumbersStyle by animateTextStyleAsState(
                targetValue = if (isExpanded) {
                    MaterialTheme.typography.displayMedium
                } else MaterialTheme.typography.headlineLarge,
            )

            val pageCount = if (state.settings.budgetAmount > 0f) 2 else 1
            val pagerState = rememberPagerState { pageCount }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AnimatedVisibility(
                        visible = isExpanded,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut(),
                        label = "AnimatedVisibilityTotalText"
                    ) {
                        Text(
                            text = stringResource(if (page == 0) MR.strings.total_spending else MR.strings.remaining),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.basicMarquee()
                        )
                    }

                    val amountToShow = if (page == 0) totalAmount else remainingAmount
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = bigNumbersStyle
                                    .toSpanStyle()
                                    .copy(color = MaterialTheme.colorScheme.onPrimary)
                            ) {
                                append(amountToShow.takeWhile { it != '.' })
                            }

                            withStyle(
                                style = littleNumbersStyle
                                    .toSpanStyle()
                                    .copy(color = MaterialTheme.colorScheme.secondaryContainer)
                            ) {
                                append(amountToShow.dropWhile { it != '.' })
                            }
                        },
                        modifier = Modifier.basicMarquee()
                    )
                }
            }

            if (pageCount > 1) {
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut(),
                    label = "AnimatedVisibilityPageIndicator"
                ) {
                    DotPageIndicator(
                        pageCount = pageCount,
                        selectedIndex = pagerState.currentPage,
                        selectedDotColor = MaterialTheme.colorScheme.onPrimary,
                        dotColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                }
            }
        }
    }
}
