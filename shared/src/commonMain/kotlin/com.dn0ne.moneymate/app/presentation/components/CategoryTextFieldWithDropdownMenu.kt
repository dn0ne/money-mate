package com.dn0ne.moneymate.app.presentation.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.dn0ne.moneymate.MR
import com.dn0ne.moneymate.app.domain.entities.spending.Category
import com.dn0ne.moneymate.app.presentation.CategoryIcons
import com.dn0ne.moneymate.app.presentation.SpendingListEvent
import com.dn0ne.moneymate.app.presentation.SpendingListState
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun CategoryTextFieldWithDropdownMenu(
    state: SpendingListState,
    newCategory: Category?,
    onEvent: (SpendingListEvent) -> Unit,
    categories: List<Category>,
    selectedCategory: Category?,
    isError: Boolean?,
    modifier: Modifier = Modifier,
    visibleItemsCount: Int = categories.size,
    showHalfOfOutOfBoundsElement: Boolean = categories.size > visibleItemsCount,
    onCategoryChanged: (Category) -> Unit
) {
    var menuExpanded by remember {
        mutableStateOf(false)
    }
    var columnSize by remember {
        mutableStateOf(Size.Zero)
    }
    var leadingIcon by remember {
        mutableStateOf(
            if (selectedCategory != null) {
                CategoryIcons.getIconByName(selectedCategory.iconName)
            } else null
        )
    }
    var chosenCategory by remember {
        mutableStateOf(selectedCategory ?: Category())
    }

    val menuVerticalPadding = 16
    val itemHeight = 48
    val itemsToShowCount =
        if (categories.size < visibleItemsCount) {
            categories.size
        } else {
            visibleItemsCount
        }
            .coerceAtLeast(1)

    val halfOfOutOfBoundsElementHeight = itemHeight / 2 + menuVerticalPadding / 8

    val menuHeight: Dp =
        (menuVerticalPadding +
                itemHeight * itemsToShowCount +
                if (showHalfOfOutOfBoundsElement)
                    halfOfOutOfBoundsElementHeight
                else 0).dp

    Column(
        modifier = modifier
            .onGloballyPositioned {
                columnSize = it.size.toSize()
            }
    ) {

        AddCategoryDialog(
            state = state,
            newCategory = newCategory,
            editing = false,
            onEvent = onEvent
        )

        OutlinedTextField(
            label = {
                Text(text = stringResource(MR.strings.category))
            },
            value = chosenCategory.name,
            placeholder = {
                Text(stringResource(MR.strings.choose_category))
            },
            onValueChange = { },
            readOnly = true,
            enabled = true,
            trailingIcon = {
                Icon(imageVector = Icons.Rounded.ArrowDropDown, contentDescription = null)
            },
            leadingIcon = leadingIcon?.let {
                {
                    Icon(imageVector = it, contentDescription = null)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            interactionSource = remember { MutableInteractionSource() }
                .also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Press) {
                                menuExpanded = !menuExpanded
                            }
                        }
                    }
                },
            supportingText = isError?.let {
                {
                    Text(
                        text = stringResource(MR.strings.category_error),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            isError = isError ?: false
        )
        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { columnSize.width.toDp() })
                .height(menuHeight)
        ) {
            for (category in categories) {
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(
                            imageVector = CategoryIcons.getIconByName(category.iconName),
                            contentDescription = null
                        )
                    },
                    text = {
                        Text(text = category.name)
                    },
                    onClick = {
                        chosenCategory = category
                        leadingIcon = CategoryIcons.getIconByName(category.iconName)
                        menuExpanded = false
                        onCategoryChanged(chosenCategory)
                    }
                )
            }

            if (categories.isNotEmpty()) {
                Divider(
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(50.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null
                    )
                },
                text = {
                    Text(text = stringResource(MR.strings.add_new_category))
                },
                onClick = {
                    onEvent(SpendingListEvent.OnAddNewCategoryClick)
                }
            )
        }
    }
}