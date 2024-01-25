package com.dn0ne.moneymate.app.domain

object SpendingValidator {

    fun validateSpending(spending: Spending): ValidationResult {
        var result = ValidationResult()

        if (spending.category == null) {
            result = result.copy(
                categoryError = true
            )
        }

        if (spending.shoppingList.isNotEmpty()) {
            result = result.copy(
                shoppingListError = checkShoppingList(spending.shoppingList)
            )
        }

        if (spending.amount == 0f) {
            if (spending.shoppingList.isEmpty()) {
                result = result.copy(
                    amountError = true
                )
            }
        }

        if (spending.shortDescription?.isBlank() == true) {
            result = result.copy(
                shortDescriptionError = true
            )
        }


        return result
    }

    private fun checkShoppingList(shoppingList: List<ShoppingItem>): Map<ShoppingItem, Pair<Boolean, Boolean>>? {
        val errors = shoppingList
            .filter { item ->
                item.name.isBlank() || item.price == 0f
            }.associateWith { item ->
                Pair(item.name.isBlank(), item.price == 0f)
            }
        return errors.ifEmpty { null }
    }

    data class ValidationResult(
        val categoryError: Boolean? = null,
        val amountError: Boolean? = null,
        val shortDescriptionError: Boolean? = null,
        val shoppingListError: Map<ShoppingItem, Pair<Boolean, Boolean>>? = null
    )
}