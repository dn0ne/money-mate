package com.dn0ne.moneymate.app.extensions

import com.dn0ne.moneymate.app.domain.ShoppingItem

fun ShoppingItem.copy(
    name: String = this.name,
    price: Float = this.price
): ShoppingItem {
    return ShoppingItem().apply {
        this.name = name
        this.price = price
    }
}