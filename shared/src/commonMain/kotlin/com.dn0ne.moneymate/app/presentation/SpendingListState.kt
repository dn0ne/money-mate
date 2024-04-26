package com.dn0ne.moneymate.app.presentation

import com.dn0ne.moneymate.app.domain.entities.spending.Category
import com.dn0ne.moneymate.app.domain.entities.spending.ShoppingItem
import com.dn0ne.moneymate.app.domain.entities.spending.Spending
import com.dn0ne.moneymate.app.domain.settings.AppSettings
import com.dn0ne.moneymate.app.domain.sync.SyncStatus
import com.dn0ne.moneymate.app.domain.validators.UserValidator
import kotlinx.datetime.LocalDate

/**
 * App state class
 */
data class SpendingListState(
    val spendings: List<Spending> = emptyList(),
    val categories: List<Category> = emptyList(),
    val appSettings: AppSettings = AppSettings(),
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

    val isUserLoggedIn: Boolean = false,
    val isSyncingInProgress: Boolean = false,
    val isSyncEndedWithError: Boolean? = null,
    val syncStatus: SyncStatus? = null,
    val isAuthSheetOpen: Boolean = false,
    val isAuthInProgress: Boolean = false,
    val isLoggingIn: Boolean = true,
    val emailError: UserValidator.ValidationError? = null,
    val passwordError: UserValidator.ValidationError? = null,
)