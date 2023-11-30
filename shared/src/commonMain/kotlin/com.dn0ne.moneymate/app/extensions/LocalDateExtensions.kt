package com.dn0ne.moneymate.app.extensions

import kotlinx.datetime.LocalDate

fun LocalDate.formatToString(): String {
    return "${dayOfWeek.name.capitalize()}, $dayOfMonth ${month.name.capitalize()}"
}