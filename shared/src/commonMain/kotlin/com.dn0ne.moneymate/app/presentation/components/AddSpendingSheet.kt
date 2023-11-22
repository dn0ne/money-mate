package com.dn0ne.moneymate.app.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBackIos
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dn0ne.moneymate.app.domain.ShoppingItem
import com.dn0ne.moneymate.app.domain.Spending
import com.dn0ne.moneymate.app.presentation.SpendingListEvent
import com.dn0ne.moneymate.app.presentation.SpendingListState
import com.dn0ne.moneymate.core.presentation.SimpleBottomSheet
import io.realm.kotlin.ext.realmListOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun AddSpendingSheet(
    state: SpendingListState,
    newSpending: Spending?,
    isOpen: Boolean,
    onEvent: (SpendingListEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    SimpleBottomSheet(
        visible = isOpen,
        modifier = modifier.fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        //-------------------------------------------------------------------------------
        //  Top Bar
        //-------------------------------------------------------------------------------
        var isTopBarCollapsed by remember {
            mutableStateOf(false)
        }
        CollapsingTopAppBar(
            isCollapsed = isTopBarCollapsed,
            title = {
                Text(text = "Spend", style = MaterialTheme.typography.headlineMedium)
            },
            collapsedTitle = {
                Text("Spend", style = MaterialTheme.typography.titleMedium)
            },
            leadingButton = {
                IconButton(
                    onClick = {
                        onEvent(SpendingListEvent.DismissSpending)
                    },
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBackIos,
                        contentDescription = "Close add spending sheet",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        )
        //-------------------------------------------------------------------------------
        //  Content
        //-------------------------------------------------------------------------------
        val coroutineScope = rememberCoroutineScope()
        val scrollState = rememberScrollState()
        var areDetailsAdded by remember {
            mutableStateOf(state.areSpendingDetailsAdded)
        }
        var detailsType by remember {
            mutableStateOf(state.detailsType)
        }
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
                .fillMaxSize()
                .scrollable(scrollableState, Orientation.Vertical)
                .verticalScroll(scrollState)
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //-------------------------------------------------------------------------------
            //  Categories Dropdown Select
            //-------------------------------------------------------------------------------
            CategoryTextFieldWithDropdownMenu(
                categories = state.categories,
                selectedCategory = newSpending?.category,
                error = state.categoryError,
                modifier = Modifier.fillMaxWidth(),
                visibleItemsCount = 3,
                onCategoryChanged = {
                    onEvent(SpendingListEvent.OnCategoryChanged(it))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            //-------------------------------------------------------------------------------
            //  Details Section
            //-------------------------------------------------------------------------------
            AnimatedVisibility(
                visible = areDetailsAdded,
                modifier = Modifier.fillMaxWidth(),
                label = "DetailsVisibilityAnimation",
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Select details type",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    //-------------------------------------------------------------------------------
                    //  Segmented button for choosing details type
                    //-------------------------------------------------------------------------------
                    SegmentedButton(
                        items = listOf("Short description", "Shopping list"),
                        modifier = Modifier.fillMaxWidth(),
                        onItemSelection = {
                            detailsType = it
                            onEvent(
                                SpendingListEvent.OnShortDescriptionChanged(
                                    if (detailsType == 0) "" else null
                                )
                            )
                            onEvent(SpendingListEvent.OnShoppingListChanged(realmListOf()))
                            onEvent(SpendingListEvent.OnAmountChanged(0f))
                            onEvent(SpendingListEvent.OnDetailsTypeChanged(detailsType))
                        },
                        defaultSelectedItemIndex = detailsType
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    //-------------------------------------------------------------------------------
                    //  Details Content
                    //-------------------------------------------------------------------------------
                    AnimatedContent(
                        targetState = detailsType,
                        transitionSpec = {
                            (fadeIn() + expandVertically()).togetherWith(fadeOut() + shrinkVertically())
                        }
                    ) { targetContent ->
                        Column(modifier = Modifier.fillMaxWidth()) {
                            when (targetContent) {
                                0 -> {
                                    //-------------------------------------------------------------------
                                    //  Short Description Text Field
                                    //-------------------------------------------------------------------
                                    var description by remember {
                                        mutableStateOf(newSpending?.shortDescription ?: "")
                                    }
                                    SpendingTextField(
                                        value = description,
                                        onValueChanged = {
                                            description = it
                                            onEvent(
                                                SpendingListEvent.OnShortDescriptionChanged(
                                                    description.trim()
                                                )
                                            )
                                        },
                                        placeholder = "Describe your purchase",
                                        error = state.shortDescriptionError,
                                        keyboardType = KeyboardType.Text
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                }

                                1 -> {
                                    //-------------------------------------------------------------------
                                    //  Shopping List
                                    //-------------------------------------------------------------------
                                    //---------------------------------------------------------------
                                    //  List Titles
                                    //---------------------------------------------------------------
                                    ShoppingListTitles(modifier = Modifier.fillMaxWidth())
                                    Spacer(modifier = Modifier.height(16.dp))

                                    val shoppingList = remember {
                                        newSpending?.shoppingList?.apply {
                                            if (isEmpty()) {
                                                add(ShoppingItem())
                                            }
                                        } ?: mutableStateListOf(ShoppingItem())
                                    }


                                    //---------------------------------------------------------------
                                    //  Initial Shopping List Item
                                    //---------------------------------------------------------------
                                    ShoppingListItem(
                                        item = shoppingList[0],
                                        onItemNameChanged = {
                                            shoppingList[0].name = it.trim()
                                            onEvent(
                                                SpendingListEvent.OnShoppingListChanged(
                                                    shoppingList
                                                )
                                            )
                                        },
                                        onItemPriceChanged = {
                                            shoppingList[0].price = it
                                            onEvent(
                                                SpendingListEvent.OnShoppingListChanged(
                                                    shoppingList
                                                )
                                            )
                                            onEvent(
                                                SpendingListEvent.OnAmountChanged(
                                                    shoppingList.map { shoppingItem ->
                                                        shoppingItem.price
                                                    }.sum()
                                                )
                                            )
                                        },
                                        itemError = state.shoppingListError?.get(shoppingList[0]),
                                        deletable = false,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    //---------------------------------------------------------------
                                    //  Additional List Items
                                    //---------------------------------------------------------------
                                    for (item in shoppingList.drop(1)) {
                                        key(item) {
                                            val visibleState = remember {
                                                MutableTransitionState(false).apply {
                                                    targetState = true
                                                }
                                            }
                                            AnimatedVisibility(
                                                visibleState = visibleState,
                                                enter = fadeIn() + expandVertically(),
                                                exit = fadeOut() + shrinkVertically(),
                                                label = "AnimatedVisibility$item"
                                            ) {
                                                Column(modifier = Modifier.fillMaxWidth()) {
                                                    ShoppingListItem(
                                                        item = item,
                                                        onItemNameChanged = {
                                                            item.name = it.trim()
                                                            onEvent(
                                                                SpendingListEvent.OnShoppingListChanged(
                                                                    shoppingList
                                                                )
                                                            )
                                                        },
                                                        onItemPriceChanged = {
                                                            item.price = it
                                                            onEvent(
                                                                SpendingListEvent.OnShoppingListChanged(
                                                                    shoppingList
                                                                )
                                                            )
                                                            onEvent(
                                                                SpendingListEvent.OnAmountChanged(
                                                                    shoppingList.map { shoppingItem ->
                                                                        shoppingItem.price
                                                                    }.sum()
                                                                )
                                                            )
                                                        },
                                                        itemError = state.shoppingListError?.get(item),
                                                        onDeleteButtonClick = {
                                                            coroutineScope.launch {
                                                                visibleState.targetState = false
                                                                delay(350) // Animation delay
                                                                shoppingList.remove(item)
                                                                onEvent(
                                                                    SpendingListEvent.OnShoppingListChanged(
                                                                        shoppingList
                                                                    )
                                                                )
                                                            }
                                                        },
                                                        modifier = Modifier.fillMaxWidth()
                                                    )
                                                    Spacer(modifier = Modifier.height(16.dp))
                                                }
                                            }
                                        }
                                    }

                                    //---------------------------------------------------------------
                                    //  Add Item Button
                                    //---------------------------------------------------------------
                                    Button(
                                        onClick = {
                                            shoppingList.add(ShoppingItem())
                                            onEvent(
                                                SpendingListEvent.OnShoppingListChanged(
                                                    shoppingList
                                                )
                                            )
                                            coroutineScope.launch {
                                                isScrollingUp = false
                                                delay(300) // Animation delay
                                                scrollState.animateScrollTo(
                                                    value = scrollState.maxValue,
                                                    animationSpec = tween()
                                                )
                                            }
                                        },
                                        colors = ButtonDefaults.filledTonalButtonColors(),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Add,
                                            contentDescription = null
                                        )
                                        Text("Add item")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            //-------------------------------------------------------------------------------
            //  Spent Amount Text Field
            //-------------------------------------------------------------------------------
            AnimatedVisibility(
                visible = detailsType != 1 || !areDetailsAdded,
                modifier = Modifier.fillMaxWidth(),
                label = "AmountTextFieldVisibilityAnimation"
            ) {
                var amount by remember {
                    mutableStateOf(
                        if (newSpending == null || newSpending.amount == 0f) {
                            ""
                        } else newSpending.amount.toString()
                    )
                }
                SpendingTextField(
                    label = "Amount spent",
                    value = amount,
                    maxLength = 7,
                    placeholder = "Enter amount",
                    error = state.amountError,
                    onValueChanged = {
                        amount = it
                        onEvent(SpendingListEvent.OnAmountChanged(amount.toFloatOrNull() ?: 0f))
                    },
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            //-------------------------------------------------------------------------------
            //  Bottom Buttons
            //-------------------------------------------------------------------------------
            Row(modifier = Modifier.fillMaxWidth()) {
                //-------------------------------------------------------------------------------
                //  Add Details Button
                //-------------------------------------------------------------------------------
                OutlinedButton(
                    onClick = {
                        areDetailsAdded = !areDetailsAdded
                        onEvent(SpendingListEvent.OnAddDetailsClick)
                        onEvent(SpendingListEvent.OnShortDescriptionChanged(if (areDetailsAdded) "" else null))
                        onEvent(SpendingListEvent.OnShoppingListChanged(realmListOf()))
                    },
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(4.dp),
                ) {
                    AnimatedContent(
                        targetState = areDetailsAdded,
                        modifier = Modifier.fillMaxWidth()
                    ) { targetState ->
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (!targetState) {
                                Icon(
                                    imageVector = Icons.Rounded.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(22.dp)
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                Text(text = "Add details")
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = null,
                                    modifier = Modifier.size(22.dp)
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                Text(text = "Remove details")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                //-------------------------------------------------------------------------------
                //  Confirm Button
                //-------------------------------------------------------------------------------
                Button(
                    onClick = {
                        onEvent(SpendingListEvent.SaveSpending)
                    },
                    modifier = Modifier.weight(1f).fillMaxWidth()
                ) {
                    Text(text = "Confirm")
                }
            }
        }
    }
}