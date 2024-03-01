package com.dn0ne.moneymate.app.domain.extensions

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.daysUntil

fun LocalDate.Companion.today(): LocalDate {
    return LocalDateTime.now().date
}

fun LocalDate.copy(
    year: Int = this.year,
    monthNumber: Int = this.monthNumber,
    dayOfMonth: Int = this.dayOfMonth
): LocalDate {
    return LocalDate(
        year, monthNumber, dayOfMonth
    )
}

fun LocalDate.getMonthDaysCount(): Int {
    var nextYear = false
    return this.copy(dayOfMonth = 1).daysUntil(this.copy(
        monthNumber = if (this.monthNumber + 1 > 12) {
            nextYear = true
            1
        } else this.monthNumber + 1,
        year = if (nextYear) this.year + 1 else this.year,
        dayOfMonth = 1
    ))
}