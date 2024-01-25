package com.dn0ne.moneymate.app.extensions

import com.dn0ne.moneymate.app.domain.Category

fun Category.copy(
    name: String = this.name,
    iconName: String = this.iconName
): Category {
    return Category(id = this.id, name = name, iconName = iconName)
}