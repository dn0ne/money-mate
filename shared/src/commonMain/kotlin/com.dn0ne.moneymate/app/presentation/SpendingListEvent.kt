package com.dn0ne.moneymate.app.presentation

import com.dn0ne.moneymate.app.domain.entities.spending.Category
import com.dn0ne.moneymate.app.domain.entities.spending.ShoppingItem
import com.dn0ne.moneymate.app.domain.entities.spending.Spending
import com.dn0ne.moneymate.app.domain.enumerations.BudgetPeriod
import com.dn0ne.moneymate.app.domain.enumerations.Theme

/**
 * Interface with app events
 */
sealed interface SpendingListEvent {
    data object OnAddNewSpendingClick: SpendingListEvent
    data object OnAddDetailsClick: SpendingListEvent
    data class OnDetailsTypeChanged(val value: Int): SpendingListEvent
    data object DismissSpending: SpendingListEvent
    data class OnCategoryChanged(val value: Category): SpendingListEvent
    data class OnAmountChanged(val value: Float): SpendingListEvent
    data class OnShortDescriptionChanged(val value: String?): SpendingListEvent
    data class OnShoppingListChanged(val list: List<ShoppingItem>): SpendingListEvent
    data class SelectSpending(val spending: Spending): SpendingListEvent
    data class EditSpending(val spending: Spending): SpendingListEvent
    data object DeleteSpending: SpendingListEvent
    data object SaveSpending: SpendingListEvent
    data object OnAddNewCategoryClick: SpendingListEvent
    data object DismissCategory: SpendingListEvent
    data class OnPickedIconNameChanged(val value: String): SpendingListEvent
    data class OnNewCategoryNameChanged(val value: String): SpendingListEvent
    data object DeleteCategory: SpendingListEvent
    data object SaveCategory: SpendingListEvent
    data class SelectCategory(val category: Category): SpendingListEvent
    data object OnSummaryClick: SpendingListEvent
    data object OnSummaryBackClick: SpendingListEvent
    data object OnSettingsClick: SpendingListEvent
    data object OnSettingsBackClick: SpendingListEvent
    data class OnThemeChanged(val theme: Theme): SpendingListEvent
    data class OnDynamicColorChanged(val dynamicColor: Boolean?): SpendingListEvent
    data class OnBudgetAmountChanged(val value: Float): SpendingListEvent
    data class OnBudgetPeriodChanged(val value: BudgetPeriod): SpendingListEvent
    data class OnBudgetPeriodStartChanged(val value: Int): SpendingListEvent
    data object OnBudgetAmountChangeClick: SpendingListEvent
    data object OnBudgetAmountChangeDismiss: SpendingListEvent
    data object OnBudgetPeriodChangeClick: SpendingListEvent
    data object OnBudgetPeriodChangeDismiss: SpendingListEvent
    data object OnPeriodStartChangeClick: SpendingListEvent
    data object OnPeriodStartChangeDismiss: SpendingListEvent
    data class OnEmailChanged(val value: String): SpendingListEvent
    data class OnPasswordChanged(val value: String): SpendingListEvent
    data object OnSignupClick: SpendingListEvent
    data object OnLoginClick: SpendingListEvent
    data object OnLogoutClick: SpendingListEvent
    data object ConfirmSignup: SpendingListEvent
    data object ConfirmLogin: SpendingListEvent
    data object OnAuthBackCLick: SpendingListEvent
    data object StartSync: SpendingListEvent
}