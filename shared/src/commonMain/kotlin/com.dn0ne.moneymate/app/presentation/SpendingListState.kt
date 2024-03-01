package com.dn0ne.moneymate.app.presentation

import com.dn0ne.moneymate.app.domain.entities.Category
import com.dn0ne.moneymate.app.domain.settings.Settings
import com.dn0ne.moneymate.app.domain.entities.ShoppingItem
import com.dn0ne.moneymate.app.domain.entities.Spending
import kotlinx.datetime.LocalDate

/**
 * App state class
 */
data class SpendingListState(
    val spendings: List<Spending> = emptyList(),
    val categories: List<Category> = emptyList(),
    val settings: Settings = Settings(),
    val isDataLoaded: Boolean = false,

    val selectedSpending: Spending? = null,
    val isSelectedSpendingSheetOpen: Boolean = false,

    val isAddSpendingSheetOpen: Boolean = false,
    val areSpendingDetailsAdded: Boolean = false,
    val detailsType: Int = 0,

    val categoryError: Boolean? = null,
    val amountError: Boolean? = null,
    val shortDescriptionError: Boolean? = null,
    val shoppingListError: Map<ShoppingItem, Pair<Boolean, Boolean>>? = null,

    val showAddCategoryDialog: Boolean = false,

    val categoryNameError: Boolean? = null,
    val categoryIconNameError: Boolean? = null,

    val isSummarySheetOpen: Boolean = false,
    val summaryTimePeriod: ClosedRange<LocalDate>? = null,

    val isSettingsSheetOpen: Boolean = false,
    val showBudgetAmountChangeDialog: Boolean = false,
    val showBudgetPeriodChangeDialog: Boolean = false,
    val showPeriodStartChangeDialog: Boolean = false,
)