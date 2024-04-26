package com.dn0ne.moneymate.app.domain.extensions

import com.dn0ne.moneymate.app.domain.entities.spending.ShoppingItem

fun ShoppingItem.copy(
    name: String = this.name,
    price: Float = this.price
): ShoppingItem {
    return ShoppingItem().apply {
        this.name = name
        this.price = price
    }
}