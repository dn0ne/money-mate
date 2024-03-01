package com.dn0ne.moneymate.app.domain.entities

import io.realm.kotlin.types.EmbeddedRealmObject

/**
 * Shopping item class
 * @property name Item name
 * @property price Item price
 */
class ShoppingItem: EmbeddedRealmObject {
    var name: String = ""
    var price: Float = .0f
}