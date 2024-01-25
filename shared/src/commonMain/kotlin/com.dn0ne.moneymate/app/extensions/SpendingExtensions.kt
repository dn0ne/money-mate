package com.dn0ne.moneymate.app.extensions

import com.dn0ne.moneymate.app.domain.Category
import com.dn0ne.moneymate.app.domain.ShoppingItem
import com.dn0ne.moneymate.app.domain.Spending
import io.realm.kotlin.types.RealmList

/**
 * Returns new Spending with same values
 */
fun Spending.copy(
    category: Category? = this.category,
    amount: Float = this.amount,
    shortDescription: String? = this.shortDescription,
    shoppingList: RealmList<ShoppingItem> = this.shoppingList
): Spending {
    return Spending(
        id = this.id,
        category = category,
        amount = amount,
        shortDescription = shortDescription,
        shoppingList = shoppingList,
        spentAt = this.spentAt
    )
}