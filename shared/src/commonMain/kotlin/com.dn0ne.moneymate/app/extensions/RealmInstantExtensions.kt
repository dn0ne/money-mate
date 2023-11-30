package com.dn0ne.moneymate.app.extensions

import io.realm.kotlin.types.RealmInstant
import kotlinx.datetime.LocalDate

fun RealmInstant.toLocalDate(): LocalDate {
    return LocalDate.fromEpochDays((epochSeconds / 60 / 60 / 24).toInt())
}