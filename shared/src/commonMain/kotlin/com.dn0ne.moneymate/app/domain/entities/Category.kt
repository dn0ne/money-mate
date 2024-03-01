package com.dn0ne.moneymate.app.domain.entities

import com.dn0ne.moneymate.app.presentation.CategoryIcons
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Index
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

/**
 * Category class
 * @property id Id of the category (provided automatically)
 * @property name Category name (by default - empty string)
 * @property iconName Category icon name, taken from [CategoryIcons] (by default - empty string)
 */
class Category(): RealmObject {
    @PrimaryKey var id = ObjectId.invoke()
    @Index var name: String = ""
    var iconName: String = ""

    /**
     * @param name Category name
     * @param iconName Category icon name, taken from [CategoryIcons]
     */
    constructor(
        id: ObjectId? = null,
        name: String,
        iconName: String
    ): this() {
        id?.let { this.id = it }
        this.name = name
        this.iconName = iconName
    }

    override fun toString(): String {
        return name
    }
}