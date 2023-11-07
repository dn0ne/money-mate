package com.dn0ne.moneymate.app.domain

object SpendingValidator {

    fun validateSpending(spending: Spending): ValidationResult {
        var result = ValidationResult()

        if (spending.category == null) {
            result = result.copy(
                categoryError = "Category can't be unselected."
            )
        }

        if (spending.shoppingList.isNotEmpty()) {
            for (item in spending.shoppingList) {
                if (item.name.isBlank() || item.price == 0f) {
                    result = result.copy(
                        shoppingListError = "All shopping list fields must be filled"
                    )
                    break
                }
            }
        }

        if (spending.amount == 0f) {
            if (spending.shoppingList.isEmpty()) {
                result = result.copy(
                    amountError = "Amount can't be empty or equal to zero."
                )
            }
        }

        if (spending.shortDescription?.isBlank() == true) {
            result = result.copy(
                shortDescriptionError = "Description can't be empty."
            )
        }


        return result
    }

    data class ValidationResult(
        val categoryError: String? = null,
        val amountError: String? = null,
        val shortDescriptionError: String? = null,
        val shoppingListError: String? = null
    )
}