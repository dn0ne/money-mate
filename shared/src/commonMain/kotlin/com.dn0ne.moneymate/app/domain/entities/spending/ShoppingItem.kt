package com.dn0ne.moneymate.app.domain.entities.spending

import io.realm.kotlin.types.EmbeddedRealmObject
import kotlinx.serialization.Serializable

/**
 * Shopping item class
 * @property name Item name
 * @property price Item price
 */
@Serializable
class ShoppingItem: EmbeddedRealmObject {
    var name: String = ""
    var price: Float = .0f
}