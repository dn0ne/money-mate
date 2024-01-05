package com.dn0ne.moneymate.app.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIos
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dn0ne.moneymate.app.domain.Spending
import com.dn0ne.moneymate.app.extensions.toLocalDate
import com.dn0ne.moneymate.app.extensions.toStringWithScale
import com.dn0ne.moneymate.app.presentation.CategoryIcons
import com.dn0ne.moneymate.app.presentation.SpendingListEvent
import com.dn0ne.moneymate.app.presentation.SpendingListState
import com.dn0ne.moneymate.core.presentation.SimpleBottomSheet
import com.dn0ne.moneymate.core.presentation.SimpleDateRangePicker
import com.dn0ne.moneymate.util.DateFormatter
import io.realm.kotlin.internal.toDuration
import kotlinx.datetime.Clock
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

        val lastSpending = state.spendings.firstOrNull()
        val currentEpochMillis by remember {
            mutableStateOf(Clock.System.now().toEpochMilliseconds())
        }
        val dateRangePickerState = rememberDateRangePickerState(
            initialSelectedStartDateMillis = lastSpending?.let {
                it.spentAt.toDuration().inWholeMilliseconds - 7 * 24 * 60 * 60 * 1000
            } ?: currentEpochMillis,
            initialSelectedEndDateMillis = lastSpending?.spentAt?.toDuration()?.inWholeMilliseconds
                ?: currentEpochMillis
        )

        var startDate by remember {
            mutableStateOf(dateRangePickerState.selectedStartDateMillis!!.toLocalDate())
        }
        var endDate by remember {
            mutableStateOf(dateRangePickerState.selectedEndDateMillis!!.toLocalDate())
        }

        var dateRangeString by remember {
            mutableStateOf(DateFormatter.formatDateRangeToString(startDate, endDate))
        }
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

                dateRangeString = DateFormatter.formatDateRangeToString(startDate, endDate)
            }
        )

        CollapsingTopAppBar(
            isCollapsed = true,
            title = {},
            collapsedTitle = {
                Text(text = "Summary", style = MaterialTheme.typography.titleMedium)
            },
            leadingButton = {
                IconButton(
                    onClick = {
                        onEvent(SpendingListEvent.OnSummaryBackClick)
                    },
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBackIos,
                        contentDescription = "Close summary sheet",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            trailingButtons = {
                IconButton(
                    onClick = {
                        showDatePicker = true
                    },
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DateRange,
                        contentDescription = "Open date picker",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
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
                "Looks like you haven't\nspent anything yet!"
            } else {
                "There were no spendings during\n$dateRangeString"
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .widthIn(max = maxContentWidth)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
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

                CategoryPieChart(
                    spendings = filteredSpendings,
                    onEvent = onEvent,
                    modifier = Modifier.fillMaxWidth()
                )
            }

        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryPieChart(
    spendings: List<Spending>,
    onEvent: (SpendingListEvent) -> Unit,
    modifier: Modifier = Modifier,
    chartSize: Dp = 250.dp,
    iconSize: Dp = 84.dp
) {
    val localDensity = LocalDensity.current
    val selectedArcColor = MaterialTheme.colorScheme.primary
    val arcColor1 = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
    val arcColor2 = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)

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

    var selectedCategoryIndex by remember {
        mutableStateOf(0)
    }
    val arcColors =
        MutableList(chartData.size) { index ->
            animateColorAsState(
                targetValue = if (index == selectedCategoryIndex) {
                    selectedArcColor
                } else if (chartData.size % 2 == 0) {
                    if (index % 2 == 0) {
                        arcColor1
                    } else {
                        arcColor2
                    }
                } else {
                    val isTail = index < selectedCategoryIndex

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
                    (if (index == selectedCategoryIndex) 70.dp else 50.dp).toPx()
                },
                animationSpec = spring(stiffness = Spring.StiffnessMedium)
            )
        }.toMutableStateList()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
        ) {

            val spacerWidth = (chartSize - iconSize) / 2
            val pagerState = rememberPagerState {
                chartData.size
            }

            LaunchedEffect(pagerState.currentPage) {
                selectedCategoryIndex = pagerState.currentPage
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.size(chartSize)
            ) { page ->
                Spacer(modifier = Modifier.width(spacerWidth))

                Icon(
                    imageVector = CategoryIcons.getIconByName(chartData[page].first.iconName),
                    contentDescription = chartData[page].first.name,
                    tint = selectedArcColor,
                    modifier = Modifier.size(iconSize)
                )
            }

            Canvas(
                modifier = Modifier.size(chartSize)
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
        }

        Spacer(modifier = Modifier.height(48.dp))

        AnimatedContent(
            targetState = selectedCategoryIndex,
            contentAlignment = Alignment.TopCenter,
            transitionSpec = { fadeIn().togetherWith(fadeOut()) }
        ) { state ->
            val selectedIndex = state.coerceIn(chartData.indices)

            val selectedCategory = chartData[selectedIndex].first
            val amountByCategory = chartData[selectedIndex].second

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .animateContentSize()
            ) {
                key(chartData[selectedIndex]) {
                    Text(
                        text = selectedCategory.name,
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

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth().padding(12.dp)
                    ) {
                        repeat(chartData.size) { index ->
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .padding(2.dp)
                                    .clip(RoundedCornerShape(100))
                                    .background(
                                        color =
                                        if (index == selectedIndex) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                            )
                        }
                    }

                    val filteredSpendings =
                        spendings.filter { it.category?.id == selectedCategory.id }
                    filteredSpendings
                        .forEachIndexed { index, spending ->
                            key(spending.id.toString()) {
                                spending.spentAt.toLocalDate().let {
                                    if (it != filteredSpendings.getOrNull(index - 1)?.spentAt?.toLocalDate()) {
                                        Text(
                                            text = DateFormatter.formatToDayOfWeekDayMonth(it),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                        )
                                    }
                                }
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
        }

    }
}