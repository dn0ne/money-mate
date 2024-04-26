package com.dn0ne.moneymate.app.domain.extensions

import com.dn0ne.moneymate.app.domain.entities.spending.Category

fun Category.copy(
    name: String = this.name,
    iconName: String = this.iconName
): Category {
    return Category(id = this.id, name = name, iconName = iconName)
}