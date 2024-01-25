package com.dn0ne.moneymate.app.extensions

import io.realm.kotlin.internal.toDuration
import io.realm.kotlin.types.RealmInstant
import kotlinx.datetime.LocalDate

/**
 * Returns RealmInstant converted to LocalDate
 */
fun RealmInstant.toLocalDate(): LocalDate {
    return LocalDate.fromEpochDays(this.toDuration().inWholeDays.toInt())
}