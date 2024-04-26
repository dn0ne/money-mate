package com.dn0ne.moneymate.app.domain.extensions

import io.realm.kotlin.internal.toDuration
import io.realm.kotlin.types.RealmInstant
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

/**
 * Returns RealmInstant converted to LocalDate
 */
fun RealmInstant.toLocalDate(): LocalDate {
    return LocalDate.fromEpochDays(this.toDuration().inWholeDays.toInt())
}

fun RealmInstant.toInstant(): Instant {
    return Instant.fromEpochSeconds(epochSeconds, nanosecondsOfSecond)
}