package com.dn0ne.moneymate.app.domain

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Index
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

/**
 * Spending class
 * @property id Spending Id (provided automatically)
 * @property category Spending category (by default - null)
 * @property amount Spending amount
 * @property shortDescription Short description of spending
 * @property shoppingList RealmList of shopping items
 * @property spentAt Spending date
 */
class Spending(): RealmObject {
    @PrimaryKey var id: ObjectId = ObjectId.invoke()
    var category: Category? = null
    var amount: Float = .0f
    var shortDescription: String? = null
    var shoppingList: RealmList<ShoppingItem> = realmListOf()
    @Index var spentAt: RealmInstant = RealmInstant.now()

    /**
     * @param id Spending id (will be generated if left null)
     * @param category Spending category
     * @param amount Spending amount
     * @param shortDescription Short description of spending
     * @param shoppingList List of shopping items
     * @param spentAt Spending date (will be current timestamp if left null)
     */
    constructor(
        id: ObjectId? = null,
        category: Category?,
        amount: Float,
        shortDescription: String? = null,
        shoppingList: List<ShoppingItem> = listOf(),
        spentAt: RealmInstant? = null
    ) : this() {
        if (id != null) this.id = id
        this.category = category
        this.amount = amount
        this.shortDescription = shortDescription
        this.shoppingList = shoppingList.toRealmList()
        if (spentAt != null) this.spentAt = spentAt
    }
}