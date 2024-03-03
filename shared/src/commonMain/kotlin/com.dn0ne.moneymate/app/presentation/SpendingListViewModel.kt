package com.dn0ne.moneymate.app.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.dn0ne.moneymate.app.domain.entities.Category
import com.dn0ne.moneymate.app.domain.entities.Spending
import com.dn0ne.moneymate.app.domain.extensions.copy
import com.dn0ne.moneymate.app.domain.repository.SpendingRepository
import com.dn0ne.moneymate.app.domain.settings.Settings
import com.dn0ne.moneymate.app.domain.validators.CategoryValidator
import com.dn0ne.moneymate.app.domain.validators.SpendingValidator
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.realm.kotlin.ext.toRealmList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * App view model class
 */
class SpendingListViewModel(private val spendingRepository: SpendingRepository) : ViewModel() {

    private val _settings = MutableStateFlow(Settings())
    private val _state = MutableStateFlow(SpendingListState())
    val state = combine(
        _state, spendingRepository.getSpendings(), spendingRepository.getCategories(), _settings
    ) { state, spendings, categories, settings ->
        state.copy(
            spendings = spendings,
            categories = categories,
            settings = settings,
            isDataLoaded = true
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), SpendingListState())

    var newSpending: Spending? by mutableStateOf(null)
        private set

    var newCategory: Category? by mutableStateOf(null)
        private set

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

                        spendingRepository.deleteSpending(id)
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
                }
            }

            is SpendingListEvent.EditSpending -> {
                _state.update {
                    it.copy(
                        isSelectedSpendingSheetOpen = false,
                        isAddSpendingSheetOpen = true,
                        areSpendingDetailsAdded = event.spending.shortDescription != null || event.spending.shoppingList.isNotEmpty(),
                        detailsType = if (event.spending.shoppingList.isNotEmpty()) 1 else 0
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
                            if (state.value.spendings.any { it.id == spending.id }) {
                                spendingRepository.updateSpending(spending)
                            } else {
                                spendingRepository.insertSpending(spending)
                            }
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

            SpendingListEvent.OnSummaryClick -> {
                _state.update {
                    it.copy(
                        isSummarySheetOpen = true
                    )
                }
            }

            SpendingListEvent.OnSummaryBackClick -> {
                _state.update {
                    it.copy(
                        isSummarySheetOpen = false
                    )
                }
            }

            SpendingListEvent.DeleteCategory -> {
                viewModelScope.launch {
                    newCategory?.id?.let { id ->
                        _state.update {
                            it.copy(
                                showAddCategoryDialog = false
                            )
                        }

                        spendingRepository.deleteCategory(id)
                        newCategory = null
                    }
                }
            }

            SpendingListEvent.DismissCategory -> {
                _state.update {
                    it.copy(
                        showAddCategoryDialog = false,
                        categoryNameError = null,
                        categoryIconNameError = null
                    )
                }
                newCategory = null
            }

            SpendingListEvent.OnAddNewCategoryClick -> {
                _state.update {
                    it.copy(
                        showAddCategoryDialog = true
                    )
                }

                newCategory = Category()
            }

            is SpendingListEvent.OnPickedIconNameChanged -> {
                newCategory = newCategory?.copy(
                    iconName = event.value
                )

                _state.update {
                    it.copy(
                        categoryIconNameError = null
                    )
                }
            }

            is SpendingListEvent.OnNewCategoryNameChanged -> {
                newCategory = newCategory?.copy(
                    name = event.value
                )

                _state.update {
                    it.copy(
                        categoryNameError = null
                    )
                }
            }

            is SpendingListEvent.SelectCategory -> {
                newCategory = event.category
            }

            SpendingListEvent.SaveCategory -> {
                newCategory?.let { category ->
                    val results = CategoryValidator.validateCategory(category)
                    val errors = listOfNotNull(
                        results.nameError,
                        results.iconNameError
                    )

                    if (errors.isEmpty()) {
                        _state.update {
                            it.copy(
                                showAddCategoryDialog = false,
                                categoryNameError = null,
                                categoryIconNameError = null
                            )
                        }

                        viewModelScope.launch {
                            if (state.value.categories.any { it.id == category.id }) {
                                spendingRepository.updateCategory(category)
                            } else {
                                spendingRepository.insertCategory(category)
                            }
                            newCategory = null
                        }

                    } else {
                        _state.update {
                            it.copy(
                                categoryNameError = results.nameError,
                                categoryIconNameError = results.iconNameError
                            )
                        }
                    }
                }
            }

            SpendingListEvent.OnSettingsClick -> {
                _state.update {
                    it.copy(
                        isSettingsSheetOpen = true
                    )
                }
            }

            SpendingListEvent.OnSettingsBackClick -> {
                _state.update {
                    it.copy(
                        isSettingsSheetOpen = false
                    )
                }
            }

            is SpendingListEvent.OnThemeChanged -> {
                _settings.update {
                    it.copy(
                        theme = event.theme
                    )
                }
            }

            is SpendingListEvent.OnDynamicColorChanged -> {
                _settings.update {
                    it.copy(
                        dynamicColor = event.dynamicColor
                    )
                }
            }

            SpendingListEvent.OnBudgetAmountChangeClick -> {
                _state.update {
                    it.copy(
                        showBudgetAmountChangeDialog = true
                    )
                }
            }

            SpendingListEvent.OnBudgetAmountChangeDismiss -> {
                _state.update {
                    it.copy(
                        showBudgetAmountChangeDialog = false,
                    )
                }
            }

            is SpendingListEvent.OnBudgetAmountChanged -> {
                _settings.update {
                    it.copy(
                        budgetAmount = event.value
                    )
                }

                _state.update {
                    it.copy(
                        showBudgetAmountChangeDialog = false,
                    )
                }
            }

            SpendingListEvent.OnBudgetPeriodChangeClick -> {
                _state.update {
                    it.copy(
                        showBudgetPeriodChangeDialog = true
                    )
                }
            }

            SpendingListEvent.OnBudgetPeriodChangeDismiss -> {
                _state.update {
                    it.copy(
                        showBudgetPeriodChangeDialog = false,
                    )
                }
            }

            is SpendingListEvent.OnBudgetPeriodChanged -> {
                _settings.update {
                    it.copy(
                        budgetPeriod = event.value
                    )
                }

                _state.update {
                    it.copy(
                        showBudgetPeriodChangeDialog = false
                    )
                }
            }

            SpendingListEvent.OnPeriodStartChangeClick -> {
                _state.update {
                    it.copy(
                        showPeriodStartChangeDialog = true
                    )
                }
            }

            SpendingListEvent.OnPeriodStartChangeDismiss -> {
                _state.update {
                    it.copy(
                        showPeriodStartChangeDialog = false
                    )
                }
            }

            is SpendingListEvent.OnBudgetPeriodStartChanged -> {
                _settings.update {
                    it.copy(
                        periodStart = event.value
                    )
                }

                _state.update {
                    it.copy(
                        showPeriodStartChangeDialog = false
                    )
                }
            }
        }
    }
}