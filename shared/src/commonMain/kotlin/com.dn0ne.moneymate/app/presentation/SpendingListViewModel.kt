package com.dn0ne.moneymate.app.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.dn0ne.moneymate.app.domain.DataSource
import com.dn0ne.moneymate.app.domain.Spending
import com.dn0ne.moneymate.app.domain.SpendingValidator
import com.dn0ne.moneymate.app.extensions.copy
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.realm.kotlin.ext.toRealmList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SpendingListViewModel(private val dataSource: DataSource) : ViewModel() {

    private val _state = MutableStateFlow(SpendingListState())
    val state = combine(
        _state, dataSource.getSpendings(), dataSource.getCategories()
    ) { state, spendings, categories ->
        state.copy(
            spendings = spendings,
            categories = categories
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), SpendingListState())
    var newSpending: Spending? by mutableStateOf(null)
        private set

    init {
        viewModelScope.launch {
            delay(5000)
            if (state.value.categories.isEmpty()) {
                insertInitialCategories()
            }
        }
    }

    fun onEvent(event: SpendingListEvent) {
        when (event) {
            SpendingListEvent.DeleteSpending -> {
                viewModelScope.launch {
                    _state.value.selectedSpending?.id?.let { id ->
                        _state.update {
                            it.copy(
                                isSelectedSpendingSheetOpen = false
                            )
                        }

                        dataSource.deleteSpending(id)
                        delay(300L) // Animation delay
                        _state.update {
                            it.copy(
                                selectedSpending = null
                            )
                        }
                    }
                }
            }

            SpendingListEvent.DismissSpending -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            isSelectedSpendingSheetOpen = false,
                            isAddSpendingSheetOpen = false,
                            areSpendingDetailsAdded = false,
                            categoryError = null,
                            amountError = null,
                            shortDescriptionError = null,
                            shoppingListError = null,
                        )
                    }

                    delay(300L) // Animation delay

                    newSpending = null
                    _state.update {
                        it.copy(
                            selectedSpending = null
                        )
                    }
                }
            }

            is SpendingListEvent.EditSpending -> {
                _state.update {
                    it.copy(
                        selectedSpending = null,
                        isAddSpendingSheetOpen = true,
                        isSelectedSpendingSheetOpen = false
                    )
                }

                newSpending = event.spending
            }

            SpendingListEvent.OnAddNewSpendingClick -> {
                _state.update {
                    it.copy(
                        isAddSpendingSheetOpen = true
                    )
                }

                newSpending = Spending(
                    category = null, amount = 0f
                )
            }

            SpendingListEvent.OnAddDetailsClick -> {
                _state.update {
                    it.copy(
                        areSpendingDetailsAdded = !it.areSpendingDetailsAdded
                    )
                }
            }

            is SpendingListEvent.OnDetailsTypeChanged -> {
                _state.update {
                    it.copy(
                        detailsType = event.value
                    )
                }
            }


            is SpendingListEvent.OnAmountChanged -> {
                newSpending = newSpending?.copy(
                    amount = event.value
                )

                _state.update {
                    it.copy(
                        amountError = null
                    )
                }
            }

            is SpendingListEvent.OnCategoryChanged -> {
                newSpending = newSpending?.copy(
                    category = event.value
                )

                _state.update {
                    it.copy(
                        categoryError = null
                    )
                }
            }

            is SpendingListEvent.OnShoppingListChanged -> {
                newSpending = newSpending?.copy(
                    shoppingList = event.list.toRealmList()
                )
            }

            is SpendingListEvent.OnShortDescriptionChanged -> {
                newSpending = newSpending?.copy(
                    shortDescription = event.value
                )

                _state.update {
                    it.copy(
                        shortDescriptionError = null
                    )
                }
            }

            SpendingListEvent.SaveSpending -> {
                newSpending?.let { spending ->
                    val result = SpendingValidator.validateSpending(spending)
                    val errors = listOfNotNull(
                        result.categoryError,
                        result.amountError,
                        result.shortDescriptionError,
                        result.shoppingListError
                    )

                    if (errors.isEmpty()) {
                        _state.update {
                            it.copy(
                                isAddSpendingSheetOpen = false,
                                areSpendingDetailsAdded = false,
                                categoryError = null,
                                amountError = null,
                                shortDescriptionError = null,
                                shoppingListError = null
                            )
                        }

                        viewModelScope.launch {
                            dataSource.insertSpending(spending)
                            delay(300L) // Animation delay
                            newSpending = null
                        }
                    } else {
                        _state.update {
                            it.copy(
                                categoryError = result.categoryError,
                                amountError = result.amountError,
                                shortDescriptionError = result.shortDescriptionError,
                                shoppingListError = result.shoppingListError
                            )
                        }
                    }
                }
            }
            is SpendingListEvent.SelectSpending -> {
                _state.update {
                    it.copy(
                        selectedSpending = event.spending,
                        isSelectedSpendingSheetOpen = true
                    )
                }
            }
        }
    }
}