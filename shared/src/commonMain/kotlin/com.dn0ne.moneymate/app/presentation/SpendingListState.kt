package com.dn0ne.moneymate.app.presentation

import com.dn0ne.moneymate.app.domain.Category
import com.dn0ne.moneymate.app.domain.Spending

data class SpendingListState(
    val spendings: List<Spending> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedSpending: Spending? = null,
    val isAddSpendingSheetOpen: Boolean = false,
    val areSpendingDetailsAdded: Boolean = false,
    val detailsType: Int = 0,
    val isSelectedSpendingSheetOpen: Boolean = false,
    val categoryError: String? = null,
    val amountError: String? = null,
    val shortDescriptionError: String? = null,
    val shoppingListError: String? = null
)