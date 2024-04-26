package com.dn0ne.moneymate.app.presentation.sheets

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIos
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dn0ne.moneymate.MR
import com.dn0ne.moneymate.app.domain.entities.spending.Spending
import com.dn0ne.moneymate.app.domain.extensions.toLocalDate
import com.dn0ne.moneymate.app.domain.extensions.toStringWithScale
import com.dn0ne.moneymate.app.domain.extensions.today
import com.dn0ne.moneymate.app.domain.util.DateFormatter
import com.dn0ne.moneymate.app.presentation.CategoryIcons
import com.dn0ne.moneymate.app.presentation.SpendingListEvent
import com.dn0ne.moneymate.app.presentation.SpendingListState
import com.dn0ne.moneymate.app.presentation.components.CollapsingTopAppBar
import com.dn0ne.moneymate.app.presentation.components.DotPageIndicator
import com.dn0ne.moneymate.app.presentation.components.SpendingListItem
import com.dn0ne.moneymate.core.presentation.ScrollUpButton
import com.dn0ne.moneymate.core.presentation.SimpleBottomSheet
import com.dn0ne.moneymate.core.presentation.SimpleDateRangePicker
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummarySheet(
    state: SpendingListState,
    isOpen: Boolean,
    onEvent: (SpendingListEvent) -> Unit,
    modifier: Modifier = Modifier,
    maxContentWidth: Dp = Dp.Unspecified
) {
    SimpleBottomSheet(
        visible = isOpen,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        var showDatePicker by remember {
            mutableStateOf(false)
        }

        val today by remember {
            mutableStateOf(LocalDate.today())
        }
        val todayEpochMillis by remember {
            mutableStateOf(
                today
                    .atStartOfDayIn(TimeZone.UTC)
                    .toEpochMilliseconds()
            )
        }
        val initialSelectedStartDateMillis by remember {
            mutableStateOf(
                state.appSettings.periodStartDate
                    .atStartOfDayIn(TimeZone.UTC)
                    .toEpochMilliseconds()
            )
        }

        val dateRangePickerState = rememberDateRangePickerState(
            initialSelectedStartDateMillis = initialSelectedStartDateMillis,
            initialSelectedEndDateMillis = todayEpochMillis
        )

        var startDate by remember {
            mutableStateOf(dateRangePickerState.selectedStartDateMillis!!.toLocalDate())
        }
        var endDate by remember {
            mutableStateOf(dateRangePickerState.selectedEndDateMillis!!.toLocalDate())
        }

        var selectedIndex by remember {
            mutableStateOf(0)
        }

        val dateRangeString = DateFormatter.formatDateRangeToString(startDate, endDate)

        SimpleDateRangePicker(
            visible = showDatePicker,
            state = dateRangePickerState,
            onDismissRequest = {
                showDatePicker = false
            },
            onCancelClick = {
                showDatePicker = false
            },
            onConfirmClick = {
                showDatePicker = false

                dateRangePickerState.selectedStartDateMillis?.let {
                    startDate = it.toLocalDate()
                }

                endDate = dateRangePickerState.selectedEndDateMillis?.toLocalDate() ?: startDate
                selectedIndex = 0
            }
        )

        CollapsingTopAppBar(
            isCollapsed = true,
            title = {},
            collapsedTitle = {
                Text(
                    text = stringResource(MR.strings.summary),
                    style = MaterialTheme.typography.titleMedium
                )
            },
            leadingButton = {
                IconButton(
                    onClick = {
                        onEvent(SpendingListEvent.OnSummaryBackClick)
                    },
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBackIos,
                        contentDescription = stringResource(MR.strings.summary_sheet_close_description),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            trailingButtons = {
                PlainTooltipBox(
                    tooltip = {
                        Text(text = stringResource(MR.strings.date_range_tooltip))
                    }
                ) {
                    IconButton(
                        onClick = {
                            showDatePicker = true
                        },
                        modifier = Modifier.tooltipAnchor()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DateRange,
                            contentDescription = stringResource(MR.strings.open_date_picker),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            },
            modifier = Modifier.widthIn(max = maxContentWidth)
        )

        val filteredSpendings = state.spendings
            .filter { it.spentAt.toLocalDate() in startDate..endDate }
            .toMutableStateList()

        val totalAmount = filteredSpendings.map { it.amount }.sum().toStringWithScale(2)

        if (filteredSpendings.size == 0) {
            val message = if (state.spendings.isEmpty()) {
                stringResource(MR.strings.no_spendings_yet)
            } else {
                stringResource(MR.strings.no_spendings_during, dateRangeString)
            }
            Box(
                modifier = Modifier
                    .widthIn(max = maxContentWidth)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            val coroutineScope = rememberCoroutineScope()
            val listState = rememberLazyListState()
            Scaffold(
                floatingActionButton = {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FloatingActionButton(
                            onClick = {},
                            content = {},
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = (100).dp)
                        )

                        ScrollUpButton(
                            visible = listState.firstVisibleItemIndex > 5,
                            onClick = {
                                coroutineScope.launch {
                                    listState.animateScrollToItem(0)
                                }
                            },
                            modifier = Modifier.align(Alignment.BottomCenter)
                                .padding(start = 32.dp)
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LazyColumn(
                        state = listState,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .widthIn(max = maxContentWidth)
                            .fillMaxWidth()
                    ) {
                        item {
                            Text(
                                text = dateRangeString,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.outline,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(
                                        style = MaterialTheme.typography.displayLarge
                                            .toSpanStyle()
                                            .copy(color = MaterialTheme.colorScheme.onSurface)
                                    ) {
                                        append(totalAmount.takeWhile { it != '.' })
                                    }

                                    withStyle(
                                        style = MaterialTheme.typography.displayMedium
                                            .toSpanStyle()
                                            .copy(color = MaterialTheme.colorScheme.primary)
                                    ) {
                                        append(totalAmount.dropWhile { it != '.' })
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(48.dp))
                        }

                        categoryPieChart(
                            spendings = filteredSpendings,
                            selectedIndex = selectedIndex,
                            onSelectedIndexChange = {
                                selectedIndex = it
                            },
                            onEvent = onEvent,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.categoryPieChart(
    spendings: List<Spending>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    onEvent: (SpendingListEvent) -> Unit,
    chartSize: Dp = 250.dp,
    iconSize: Dp = 84.dp
) {
    val totalAmount = spendings.map { it.amount }.sum()
    val chartData =
        spendings
            .asSequence()
            .map { it.category }
            .filterNotNull()
            .toHashSet()
            .map { category ->
                category to spendings
                    .filter { spending -> spending.category?.id == category.id }
                    .map { spending -> spending.amount }
                    .sum()
            }
            .sortedByDescending { it.second }
            .toMutableStateList()

    var selectedCategory = chartData[selectedIndex].first
    var amountByCategory: Float

    item {
        val localDensity = LocalDensity.current
        val selectedArcColor = MaterialTheme.colorScheme.primary
        val arcColor1 = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
        val arcColor2 = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)

        val arcColors =
            MutableList(chartData.size) { index ->
                animateColorAsState(
                    targetValue = if (index == selectedIndex) {
                        selectedArcColor
                    } else if (chartData.size % 2 == 0) {
                        if (index % 2 == 0) {
                            arcColor1
                        } else {
                            arcColor2
                        }
                    } else {
                        val isTail = index < selectedIndex

                        if (index % 2 == 0) {
                            if (isTail) arcColor1 else arcColor2
                        } else {
                            if (isTail) arcColor2 else arcColor1
                        }
                    }
                )
            }.toMutableStateList()
        val arcStrokeWidths =
            MutableList(chartData.size) { index ->
                animateFloatAsState(
                    targetValue = with(localDensity) {
                        (if (index == selectedIndex) 70.dp else 50.dp).toPx()
                    },
                    animationSpec = spring(stiffness = Spring.StiffnessMedium)
                )
            }.toMutableStateList()

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            val pagerState = rememberPagerState {
                chartData.size
            }

            LaunchedEffect(selectedIndex) {
                pagerState.animateScrollToPage(selectedIndex)
            }

            LaunchedEffect(pagerState.currentPage) {
                onSelectedIndexChange(pagerState.currentPage.coerceIn(chartData.indices))
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .width(chartSize)
                    .height(chartSize + 150.dp)
                    .align(Alignment.TopCenter)
            ) { page ->
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(chartSize)
                            .align(Alignment.TopCenter)
                    ) {
                        Icon(
                            imageVector = CategoryIcons.getIconByName(chartData[page].first.iconName),
                            contentDescription = chartData[page].first.name,
                            tint = selectedArcColor,
                            modifier = Modifier.size(iconSize)
                        )
                    }
                }
            }

            Canvas(
                modifier = Modifier.size(chartSize).align(Alignment.TopCenter)
            ) {
                var lastAngle = 0f
                chartData.forEachIndexed { index, categoryTotalPair ->
                    val arcAngle = categoryTotalPair.second / totalAmount * 360f

                    drawArc(
                        color = arcColors[index].value,
                        startAngle = lastAngle,
                        sweepAngle = arcAngle,
                        useCenter = false,
                        style = Stroke(
                            width = arcStrokeWidths[index].value,
                            cap = StrokeCap.Butt
                        )
                    )

                    lastAngle += arcAngle
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)
            ) {

                selectedCategory = chartData[selectedIndex].first
                amountByCategory = chartData[selectedIndex].second

                AnimatedContent(
                    targetState = selectedCategory,
                    transitionSpec = { fadeIn() togetherWith fadeOut(tween(0)) },
                ) { category ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = MaterialTheme.typography.titleLarge
                                        .toSpanStyle()
                                        .copy(color = MaterialTheme.colorScheme.onSurface)
                                ) {
                                    append(amountByCategory.toStringWithScale(2))
                                }

                                withStyle(
                                    style = MaterialTheme.typography.titleLarge
                                        .toSpanStyle()
                                        .copy(color = MaterialTheme.colorScheme.outline)
                                ) {
                                    append(" / ${(amountByCategory / totalAmount * 100).roundToInt()}%")
                                }
                            }
                        )
                    }

                }

                DotPageIndicator(
                    pageCount = chartData.size,
                    selectedIndex = selectedIndex,
                    modifier = Modifier.fillMaxWidth().padding(12.dp)
                )
            }
        }


    }

    val filteredSpendings = spendings.filter { it.category?.id == selectedCategory.id }
        .groupBy { it.spentAt.toLocalDate() }

    filteredSpendings.forEach { (date, spendings) ->
        stickyHeader(key = date.toString()) {
            val visibleState by remember {
                mutableStateOf(MutableTransitionState(false).apply { targetState = true })
            }
            AnimatedVisibility(
                visibleState = visibleState,
                enter = fadeIn()
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.surface)
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
        }
        items(
            items = spendings,
            key = { it.id.toHexString() }
        ) { spending ->
            val visibleState = MutableTransitionState(false).apply { targetState = true }

            AnimatedVisibility(
                visibleState = remember {
                    visibleState
                },
                enter = fadeIn()
            ) {
                SpendingListItem(
                    spending = spending,
                    modifier = Modifier
                        .fillMaxWidth()
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

    }

}