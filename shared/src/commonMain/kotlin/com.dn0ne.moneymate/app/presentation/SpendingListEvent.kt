package com.dn0ne.moneymate.app.presentation

import com.dn0ne.moneymate.app.domain.Category
import com.dn0ne.moneymate.app.domain.ShoppingItem
import com.dn0ne.moneymate.app.domain.Spending

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
}