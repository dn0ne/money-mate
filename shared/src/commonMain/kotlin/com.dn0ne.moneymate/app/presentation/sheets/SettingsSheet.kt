package com.dn0ne.moneymate.app.presentation.sheets

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBackIos
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.EventRepeat
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Login
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.QuestionAnswer
import androidx.compose.material.icons.rounded.ReportProblem
import androidx.compose.material.icons.rounded.TipsAndUpdates
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.PlainTooltipState
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dn0ne.moneymate.MR
import com.dn0ne.moneymate.app.domain.entities.spending.Category
import com.dn0ne.moneymate.app.domain.enumerations.BudgetPeriod
import com.dn0ne.moneymate.app.domain.enumerations.Theme
import com.dn0ne.moneymate.app.domain.extensions.toStringWithScale
import com.dn0ne.moneymate.app.domain.util.DateFormatter
import com.dn0ne.moneymate.app.domain.util.DecimalFormatter
import com.dn0ne.moneymate.app.presentation.CategoryIcons
import com.dn0ne.moneymate.app.presentation.SpendingListEvent
import com.dn0ne.moneymate.app.presentation.SpendingListState
import com.dn0ne.moneymate.app.presentation.components.AddCategoryDialog
import com.dn0ne.moneymate.app.presentation.components.CollapsingTopAppBar
import com.dn0ne.moneymate.app.presentation.components.SettingsItem
import com.dn0ne.moneymate.app.presentation.components.SpendingTextField
import com.dn0ne.moneymate.core.presentation.BackGestureHandler
import com.dn0ne.moneymate.core.presentation.SimpleBottomSheet
import com.dn0ne.moneymate.core.presentation.SimpleDialog
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlin.math.roundToInt

@Composable
fun SettingsSheet(
    state: SpendingListState,
    newCategory: Category?,
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
        val dividerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(50.dp)
        var isTopBarCollapsed by remember {
            mutableStateOf(false)
        }
        var topBarTitle by remember {
            mutableStateOf("")
        }

        var onBackClick by remember {
            mutableStateOf(
                {
                    onEvent(SpendingListEvent.OnSettingsBackClick)
                }
            )
        }

        var settingsPage by remember {
            mutableStateOf(SettingsPage.SETTINGS)
        }

        BackGestureHandler {
            if (settingsPage != SettingsPage.SETTINGS) {
                settingsPage = SettingsPage.SETTINGS
            } else {
                onEvent(SpendingListEvent.OnSettingsBackClick)
            }
        }

        CollapsingTopAppBar(
            isCollapsed = isTopBarCollapsed,
            title = {
                AnimatedContent(
                    targetState = topBarTitle
                ) {
                    Text(text = it, style = MaterialTheme.typography.headlineMedium)
                }
            },
            collapsedTitle = {
                AnimatedContent(
                    targetState = topBarTitle
                ) {
                    Text(text = it, style = MaterialTheme.typography.titleMedium)
                }
            },
            leadingButton = {
                IconButton(
                    onClick = onBackClick,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBackIos,
                        contentDescription = stringResource(MR.strings.settings_sheet_close_description),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            modifier = Modifier.widthIn(max = maxContentWidth)
        )

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

        Column(
            modifier = Modifier
                .widthIn(max = maxContentWidth)
                .fillMaxSize()
                .scrollable(scrollableState, Orientation.Vertical)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedContent(
                targetState = settingsPage,
                modifier = Modifier.animateContentSize()
            ) { page ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .background(color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                ) {
                    when (page) {
                        SettingsPage.SETTINGS -> {
                            topBarTitle = stringResource(MR.strings.settings)
                            onBackClick = {
                                onEvent(SpendingListEvent.OnSettingsBackClick)
                            }

                            var isThemeDialogVisible by remember {
                                mutableStateOf(false)
                            }

                            ThemeDialog(
                                visible = isThemeDialogVisible,
                                state = state,
                                onEvent = onEvent,
                                onDismissRequest = {
                                    isThemeDialogVisible = false
                                }
                            )

                            SettingsItem(
                                itemName = stringResource(MR.strings.theme),
                                icon = Icons.Rounded.DarkMode,
                                onClick = {
                                    isThemeDialogVisible = true
                                }
                            )

                            Divider(
                                color = dividerColor,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )

                            SettingsItem(
                                itemName = stringResource(MR.strings.categories),
                                icon = Icons.Rounded.Category,
                                onClick = {
                                    settingsPage = SettingsPage.CATEGORIES
                                }
                            )

                            Divider(
                                color = dividerColor,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )

                            SettingsItem(
                                itemName = stringResource(MR.strings.budget),
                                icon = Icons.Rounded.AccountBalanceWallet,
                                onClick = {
                                    settingsPage = SettingsPage.BUDGET
                                }
                            )

                            Divider(
                                color = dividerColor,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )

                            SettingsItem(
                                itemName = stringResource(MR.strings.account),
                                icon = Icons.Rounded.AccountCircle,
                                onClick = {
                                    settingsPage = SettingsPage.ACCOUNT
                                }
                            )

                            Divider(
                                color = dividerColor,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )

                            Divider(
                                color = dividerColor,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )

                            SettingsItem(
                                itemName = stringResource(MR.strings.feedback),
                                icon = Icons.Rounded.QuestionAnswer,
                                onClick = {
                                    settingsPage = SettingsPage.FEEDBACK
                                }
                            )

                        }

                        SettingsPage.CATEGORIES -> {
                            topBarTitle = stringResource(MR.strings.categories)
                            onBackClick = {
                                settingsPage = SettingsPage.SETTINGS
                            }

                            val coroutineScope = rememberCoroutineScope()
                            var editingCategory by remember {
                                mutableStateOf(true)
                            }

                            AddCategoryDialog(
                                state = state,
                                newCategory = newCategory,
                                editing = editingCategory,
                                onEvent = onEvent
                            )

                            state.categories.forEach { category ->
                                key(category.id.toHexString()) {

                                    val visibleState = remember {
                                        MutableTransitionState(false).apply {
                                            targetState = true
                                        }
                                    }

                                    AnimatedVisibility(
                                        visibleState = visibleState,
                                        enter = expandVertically() + fadeIn(),
                                        exit = shrinkVertically() + fadeOut(),
                                        label = "AnimatedVisibility${category.name}"
                                    ) {
                                        var isDeleteDialogVisible by remember {
                                            mutableStateOf(false)
                                        }
                                        DeleteCategoryDialog(
                                            visible = isDeleteDialogVisible,
                                            onDismissRequest = {
                                                isDeleteDialogVisible = false
                                                onEvent(SpendingListEvent.DismissCategory)
                                            },
                                            onConfirm = {
                                                coroutineScope.launch {
                                                    isDeleteDialogVisible = false
                                                    visibleState.targetState = false
                                                    delay(350)
                                                    onEvent(SpendingListEvent.DeleteCategory)
                                                }
                                            }
                                        )

                                        CategoryItem(
                                            category = category,
                                            onEditClick = {
                                                editingCategory = true
                                                onEvent(SpendingListEvent.OnAddNewCategoryClick)
                                                onEvent(SpendingListEvent.SelectCategory(category))
                                            },
                                            onDeleteClick = {
                                                onEvent(SpendingListEvent.SelectCategory(category))
                                                isDeleteDialogVisible = true

                                            }
                                        )
                                    }
                                }
                            }

                            FilledTonalButton(
                                onClick = {
                                    editingCategory = false
                                    onEvent(SpendingListEvent.OnAddNewCategoryClick)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 28.dp, vertical = 16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(text = stringResource(MR.strings.add_category))
                            }
                        }

                        SettingsPage.BUDGET -> {
                            topBarTitle = stringResource(MR.strings.budget)
                            onBackClick = {
                                settingsPage = SettingsPage.SETTINGS
                            }

                            ChangeBudgetAmountDialog(
                                state = state,
                                onDismissRequest = {
                                    onEvent(SpendingListEvent.OnBudgetAmountChangeDismiss)
                                },
                                onConfirm = {
                                    onEvent(SpendingListEvent.OnBudgetAmountChanged(it))
                                }
                            )

                            SettingsItem(
                                itemName = stringResource(MR.strings.amount),
                                supportingString = state.appSettings.budgetAmount.toStringWithScale(
                                    2
                                ),
                                icon = Icons.Rounded.AccountBalanceWallet,
                                onClick = {
                                    onEvent(SpendingListEvent.OnBudgetAmountChangeClick)
                                }
                            )

                            Divider(
                                color = dividerColor,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )

                            ChangeBudgetPeriodDialog(
                                state = state,
                                onDismissRequest = {
                                    onEvent(SpendingListEvent.OnBudgetPeriodChangeDismiss)
                                },
                                onConfirm = {
                                    onEvent(SpendingListEvent.OnBudgetPeriodChanged(it))
                                }
                            )

                            SettingsItem(
                                itemName = stringResource(MR.strings.period),
                                supportingString = state.appSettings.budgetPeriod.localizedName,
                                icon = Icons.Rounded.CalendarMonth,
                                onClick = {
                                    onEvent(SpendingListEvent.OnBudgetPeriodChangeClick)
                                }
                            )

                            AnimatedVisibility(
                                visible = state.appSettings.budgetPeriod != BudgetPeriod.DAY,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Divider(
                                    color = dividerColor,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                )

                                ChangePeriodStartDialog(
                                    state = state,
                                    onDismissRequest = {
                                        onEvent(SpendingListEvent.OnPeriodStartChangeDismiss)
                                    },
                                    onConfirm = {
                                        onEvent(SpendingListEvent.OnBudgetPeriodStartChanged(it))
                                    }
                                )

                                SettingsItem(
                                    itemName = stringResource(MR.strings.first_day),
                                    supportingString = when (state.appSettings.budgetPeriod) {
                                        BudgetPeriod.MONTH -> {
                                            (state.appSettings.periodStart + 1).toString()
                                        }

                                        BudgetPeriod.WEEK -> {
                                            DateFormatter.dayOfWeekLocalized(DayOfWeek.entries[state.appSettings.periodStart])
                                        }

                                        else -> ""
                                    },
                                    icon = Icons.Rounded.EventRepeat,
                                    onClick = {
                                        onEvent(SpendingListEvent.OnPeriodStartChangeClick)
                                    }
                                )
                            }
                        }

                        SettingsPage.ACCOUNT -> {
                            topBarTitle = stringResource(MR.strings.account)
                            onBackClick = {
                                settingsPage = SettingsPage.SETTINGS
                            }

                            AnimatedContent(
                                targetState = state.isUserLoggedIn
                            ) { isLoggedIn ->
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    if (isLoggedIn) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(72.dp)
                                                .clip(RoundedCornerShape(28.dp))
                                                .background(
                                                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                                        5.dp
                                                    )
                                                )
                                                .padding(horizontal = 16.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.AccountCircle,
                                                contentDescription = null,
                                                modifier = Modifier.size(48.dp)
                                            )
                                            Column {
                                                Text(
                                                    text = stringResource(MR.strings.logged_in_as),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = state.appSettings.loggedInAs ?: "",
                                                    style = MaterialTheme.typography.titleLarge,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                        OutlinedButton(
                                            onClick = {
                                                onEvent(SpendingListEvent.OnLogoutClick)
                                            },
                                            shape = RoundedCornerShape(28.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(72.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Logout,
                                                contentDescription = null
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(text = stringResource(MR.strings.log_out))
                                        }
                                    } else {
                                        Button(
                                            onClick = {
                                                onEvent(SpendingListEvent.OnLoginClick)
                                            },
                                            shape = RoundedCornerShape(28.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(72.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Login,
                                                contentDescription = null
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(text = stringResource(MR.strings.log_in))
                                        }

                                        FilledTonalButton(
                                            onClick = {
                                                onEvent(SpendingListEvent.OnSignupClick)
                                            },
                                            shape = RoundedCornerShape(28.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(72.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.PersonAdd,
                                                contentDescription = null
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(text = stringResource(MR.strings.sign_up))
                                        }
                                    }
                                }
                            }

                        }

                        SettingsPage.FEEDBACK -> {
                            topBarTitle = stringResource(MR.strings.feedback)
                            onBackClick = {
                                settingsPage = SettingsPage.SETTINGS
                            }

                            Text(
                                text = buildAnnotatedString {
                                    val mStyle =
                                        MaterialTheme.typography.headlineSmall.toSpanStyle()
                                            .copy(color = MaterialTheme.colorScheme.primary)

                                    val otherStyle =
                                        MaterialTheme.typography.headlineSmall.toSpanStyle()
                                            .copy(color = MaterialTheme.colorScheme.onSurface)

                                    val byStyle = MaterialTheme.typography.bodySmall.toSpanStyle()
                                        .copy(color = MaterialTheme.colorScheme.onSurface)

                                    withStyle(style = mStyle) {
                                        append("M")
                                    }

                                    withStyle(style = otherStyle) {
                                        append("oney")
                                    }

                                    withStyle(style = mStyle) {
                                        append("M")
                                    }

                                    withStyle(style = otherStyle) {
                                        append("ate")
                                    }

                                    withStyle(style = byStyle) {
                                        append("\nby dn0ne")
                                    }
                                },
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp,
                                modifier = Modifier.fillMaxWidth().padding(16.dp)
                            )

                            Column(
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .padding(bottom = 16.dp)
                            ) {
                                val uriHandler = LocalUriHandler.current
                                val suggestFeatureUri =
                                    stringResource(MR.strings.suggest_feature_mailto)
                                val reportIssueUri = stringResource(MR.strings.report_issue_mailto)

                                OutlinedButton(
                                    onClick = {
                                        uriHandler.openUri(suggestFeatureUri)
                                    },
                                    shape = RoundedCornerShape(28.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(72.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.TipsAndUpdates,
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = stringResource(MR.strings.suggest_a_feature))
                                }

                                OutlinedButton(
                                    onClick = {
                                        uriHandler.openUri(reportIssueUri)
                                    },
                                    shape = RoundedCornerShape(28.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(72.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.ReportProblem,
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = stringResource(MR.strings.report_an_issue))
                                }

                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeDialog(
    visible: Boolean,
    state: SpendingListState,
    onEvent: (SpendingListEvent) -> Unit,
    onDismissRequest: () -> Unit
) {
    SimpleDialog(
        visible = visible,
        onDismissRequest = onDismissRequest,
        contentAlignment = Alignment.Start,
        modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp)
    ) {

        Text(
            text = stringResource(MR.strings.app_theme),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        var selectedIndex by remember {
            mutableStateOf(
                state.appSettings.theme.ordinal
            )
        }
        Theme.entries.forEachIndexed { index, theme ->
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = index == selectedIndex,
                    onClick = {
                        selectedIndex = index
                    }
                )
                Text(
                    text = theme.localizedName,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(100))
                        .clickable {
                            selectedIndex = index
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        var useDynamicColor by remember {
            mutableStateOf(
                state.appSettings.dynamicColor
            )
        }

        useDynamicColor?.let {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = it,
                    onCheckedChange = { checked ->
                        useDynamicColor = checked
                    }
                )

                Text(
                    text = stringResource(MR.strings.use_dynamic_color),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(100))
                        .clickable {
                            useDynamicColor = !it
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }


        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(text = stringResource(MR.strings.cancel))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    onEvent(
                        SpendingListEvent.OnThemeChanged(
                            Theme.entries[selectedIndex]
                        )
                    )
                    onEvent(
                        SpendingListEvent.OnDynamicColorChanged(
                            useDynamicColor
                        )
                    )

                    onDismissRequest()
                }
            ) {
                Text(text = stringResource(MR.strings.apply))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryItem(
    category: Category,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp)
            .padding(top = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = CategoryIcons.getIconByName(category.iconName),
                contentDescription = category.name,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = category.name,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            PlainTooltipBox(
                tooltip = {
                    Text(text = stringResource(MR.strings.edit_tooltip))
                }
            ) {
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.tooltipAnchor()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = stringResource(MR.strings.edit_category)
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            PlainTooltipBox(
                tooltip = {
                    Text(text = stringResource(MR.strings.delete_tooltip))
                }
            ) {
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.tooltipAnchor()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = stringResource(MR.strings.delete_category)
                    )
                }
            }
        }
    }
}

@Composable
fun DeleteCategoryDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    SimpleDialog(
        visible = visible,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Warning,
            contentDescription = null,
            modifier = Modifier
                .padding(top = 8.dp)
                .size(24.dp)
        )

        Text(
            text = stringResource(MR.strings.delete_category_question),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = stringResource(MR.strings.delete_category_warning),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(text = stringResource(MR.strings.cancel))
            }

            TextButton(
                onClick = onConfirm
            ) {
                Text(text = stringResource(MR.strings.delete))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeBudgetAmountDialog(
    state: SpendingListState,
    onDismissRequest: () -> Unit,
    onConfirm: (Float) -> Unit,
) {
    SimpleDialog(
        visible = state.showBudgetAmountChangeDialog,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp)
    ) {
        Box(
            contentAlignment = Alignment.CenterEnd,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(MR.strings.change_amount),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Center)
            )

            val tooltipState = remember {
                PlainTooltipState()
            }
            val coroutineScope = rememberCoroutineScope()
            val tip = stringResource(MR.strings.amount_tip)
            PlainTooltipBox(
                tooltipState = tooltipState,
                tooltip = {
                    Text(text = tip)
                },
            ) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            tooltipState.show()
                        }
                    },
                    modifier = Modifier.tooltipAnchor()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = tip
                    )
                }
            }
        }

        var amount by remember {
            mutableStateOf(
                state.appSettings.budgetAmount.toString()
            )
        }

        var isError by remember {
            mutableStateOf(false)
        }

        SpendingTextField(
            value = amount,
            onValueChanged = {
                amount = DecimalFormatter.cleanup(it)
                isError = false
            },
            placeholder = stringResource(MR.strings.set_amount),
            maxLength = 7,
            error = if (isError) {
                stringResource(MR.strings.budget_amount_error)
            } else null,
            keyboardType = KeyboardType.Number,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(text = stringResource(MR.strings.cancel))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    if (amount.isBlank()) {
                        isError = true
                    } else {
                        onConfirm(amount.toFloatOrNull() ?: 0f)
                    }
                }
            ) {
                Text(text = stringResource(MR.strings.confirm))
            }
        }
    }
}

@Composable
fun ChangeBudgetPeriodDialog(
    state: SpendingListState,
    onDismissRequest: () -> Unit,
    onConfirm: (BudgetPeriod) -> Unit,
) {
    SimpleDialog(
        visible = state.showBudgetPeriodChangeDialog,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp)
    ) {
        Text(
            text = stringResource(MR.strings.change_budget_period),
            style = MaterialTheme.typography.titleLarge
        )

        var selectedIndex by remember {
            mutableStateOf(
                state.appSettings.budgetPeriod.ordinal
            )
        }
        BudgetPeriod.entries.forEachIndexed { index, period ->
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = index == selectedIndex,
                    onClick = {
                        selectedIndex = index
                    }
                )
                Text(
                    text = period.localizedName,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(100))
                        .clickable {
                            selectedIndex = index
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(text = stringResource(MR.strings.cancel))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    onConfirm(BudgetPeriod.entries[selectedIndex])
                }
            ) {
                Text(text = stringResource(MR.strings.confirm))
            }
        }
    }
}

@Composable
fun ChangePeriodStartDialog(
    state: SpendingListState,
    onDismissRequest: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    SimpleDialog(
        visible = state.showPeriodStartChangeDialog,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(
            text = stringResource(MR.strings.change_first_day),
            style = MaterialTheme.typography.titleLarge
        )
        val month by remember {
            mutableStateOf(0..30)
        }
        val weekDays =
            listOf(
                stringResource(MR.strings.monday_short),
                stringResource(MR.strings.tuesday_short),
                stringResource(MR.strings.wednesday_short),
                stringResource(MR.strings.thursday_short),
                stringResource(MR.strings.friday_short),
                stringResource(MR.strings.saturday_short),
                stringResource(MR.strings.sunday_short),
            )

        val cellSize by remember {
            mutableStateOf(40.dp)
        }
        val itemSpacing by remember {
            mutableStateOf(6.dp)
        }

        val textStyle = MaterialTheme.typography.bodyMedium

        val selectedBackgroundColor = MaterialTheme.colorScheme.primary
        val selectedTextColor = MaterialTheme.colorScheme.onPrimary
        val selectedBorderColor = Color.Transparent

        val previousTextColor = MaterialTheme.colorScheme.primary
        val previousBorderColor = MaterialTheme.colorScheme.primary

        val backgroundColor = Color.Transparent
        val textColor = MaterialTheme.colorScheme.onSurface
        val borderColor = Color.Transparent

        var selectedDay by remember {
            mutableStateOf(
                state.appSettings.periodStart
            )
        }
        when (state.appSettings.budgetPeriod) {
            BudgetPeriod.MONTH -> {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    month.chunked(7).forEach { week ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(itemSpacing),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = itemSpacing)
                        ) {
                            week.forEach { day ->
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(cellSize)
                                        .clip(RoundedCornerShape(100))
                                        .background(
                                            color = animateColorAsState(
                                                targetValue = if (day == selectedDay) {
                                                    selectedBackgroundColor
                                                } else {
                                                    backgroundColor
                                                }
                                            ).value
                                        )
                                        .border(
                                            border = BorderStroke(
                                                width = 1.dp,
                                                color = animateColorAsState(
                                                    targetValue = when (day) {
                                                        selectedDay -> {
                                                            selectedBorderColor
                                                        }

                                                        state.appSettings.periodStart -> {
                                                            previousBorderColor
                                                        }

                                                        else -> {
                                                            borderColor
                                                        }
                                                    }
                                                ).value
                                            ),
                                            shape = RoundedCornerShape(100)
                                        )
                                        .clickable {
                                            selectedDay = day
                                        }
                                ) {
                                    Text(
                                        text = (day + 1).toString(),
                                        style = textStyle,
                                        color = animateColorAsState(
                                            targetValue = when (day) {
                                                selectedDay -> {
                                                    selectedTextColor
                                                }

                                                state.appSettings.periodStart -> {
                                                    previousTextColor
                                                }

                                                else -> {
                                                    textColor
                                                }
                                            }
                                        ).value
                                    )
                                }
                            }
                        }
                    }
                }
            }

            BudgetPeriod.WEEK -> {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(itemSpacing),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    weekDays.forEachIndexed { index, dayName ->
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(cellSize)
                                .clip(RoundedCornerShape(100))
                                .background(
                                    color = animateColorAsState(
                                        targetValue = if (index == selectedDay) {
                                            selectedBackgroundColor
                                        } else {
                                            backgroundColor
                                        }
                                    ).value
                                )
                                .border(
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = animateColorAsState(
                                            targetValue = when (index) {
                                                selectedDay -> {
                                                    selectedBorderColor
                                                }

                                                state.appSettings.periodStart -> {
                                                    previousBorderColor
                                                }

                                                else -> {
                                                    borderColor
                                                }
                                            }
                                        ).value
                                    ),
                                    shape = RoundedCornerShape(100)
                                )
                                .clickable {
                                    selectedDay = index
                                }
                        ) {
                            Text(
                                text = dayName,
                                style = textStyle,
                                color = animateColorAsState(
                                    targetValue = when (index) {
                                        selectedDay -> {
                                            selectedTextColor
                                        }

                                        state.appSettings.periodStart -> {
                                            previousTextColor
                                        }

                                        weekDays.lastIndex -> {
                                            MaterialTheme.colorScheme.error
                                        }

                                        else -> {
                                            textColor
                                        }
                                    }
                                ).value
                            )
                        }
                    }
                }
            }

            else -> onDismissRequest()
        }

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(text = stringResource(MR.strings.cancel))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    onConfirm(selectedDay)
                }
            ) {
                Text(text = stringResource(MR.strings.confirm))
            }
        }
    }
}

private enum class SettingsPage {
    SETTINGS, CATEGORIES, BUDGET, FEEDBACK, ACCOUNT
}