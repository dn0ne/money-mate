package com.dn0ne.moneymate.app.extensions

import kotlinx.datetime.LocalDate
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun Long.toLocalDate(): LocalDate {
    return LocalDate.fromEpochDays(this.toDuration(DurationUnit.MILLISECONDS).inWholeDays.toInt())
}