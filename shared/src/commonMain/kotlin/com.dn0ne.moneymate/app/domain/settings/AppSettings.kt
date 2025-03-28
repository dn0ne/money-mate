package com.dn0ne.moneymate.app.domain.settings

import com.dn0ne.moneymate.app.domain.enumerations.BudgetPeriod
import com.dn0ne.moneymate.app.domain.enumerations.Theme
import com.dn0ne.moneymate.app.domain.extensions.copy
import com.dn0ne.moneymate.app.domain.extensions.getMonthDaysCount
import com.dn0ne.moneymate.app.domain.extensions.today
import com.russhwolf.settings.Settings
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus


class AppSettings() {
    private val settings = Settings()

    var theme: Theme
        get() = Theme.valueOf(settings.getString("theme", Theme.SYSTEM.name))
        set(value) {
            settings.putString("theme", value.name)
        }

    var dynamicColor: Boolean?
        get() = settings.getBooleanOrNull("dynamic")
        set(value) {
            value?.let {
                settings.putBoolean("dynamic", value)
            }
        }

    var budgetAmount: Float
        get() = settings.getFloatOrNull("budget") ?: 0f
        set(value) {
            settings.putFloat("budget", value)
        }

    var budgetPeriod: BudgetPeriod
        get() = BudgetPeriod.valueOf(settings.getString("period", BudgetPeriod.MONTH.name))
        set(value) {
            settings.putString("period", value.name)
        }

    var periodStart: Int
        get() = settings.getInt("start", 0)
        set(value) {
            settings.putInt(
                key = "start",
                value = value.coerceIn(
                    0..(
                            if (budgetPeriod == BudgetPeriod.WEEK) 6
                            else LocalDate.today().getMonthDaysCount() - 1
                            )
                )
            )
        }

    val periodStartDate: LocalDate
        get() {
            return LocalDate.today().let {
                when (budgetPeriod) {
                    BudgetPeriod.MONTH -> {
                        var previousYear = false
                        it.copy(
                            monthNumber = if (periodStart + 1 <= it.dayOfMonth) {
                                it.monthNumber
                            } else {
                                (it.monthNumber - 1).let { previousMonthNumber ->
                                    if (previousMonthNumber < 1) {
                                        previousYear = true
                                        12
                                    } else previousMonthNumber
                                }
                            },
                            year = if (previousYear) it.year - 1 else it.year,
                            dayOfMonth = periodStart + 1
                        )
                    }

                    BudgetPeriod.WEEK -> {
                        it
                            .minus(
                                DatePeriod(
                                    days = it.dayOfWeek.ordinal.let { todayOrdinal ->
                                        if (periodStart <= todayOrdinal) {
                                            todayOrdinal
                                        } else todayOrdinal + 7
                                    }
                                )
                            )
                            .plus(DatePeriod(days = periodStart))
                    }

                    BudgetPeriod.DAY -> {
                        it
                    }
                }
            }
        }

    var loggedInAs: String?
        get() = settings.getStringOrNull("loggedInAs")
        set(value) {
            value?.let {
                settings.putString("loggedInAs", it)
            } ?: settings.remove("loggedInAs")
        }

    constructor(
        theme: Theme = Theme.SYSTEM,
        dynamicColor: Boolean? = null,
        budgetAmount: Float = 0f,
        budgetPeriod: BudgetPeriod = BudgetPeriod.MONTH,
        periodStart: Int = 0,
        loggedInAs: String? = null
    ) : this() {
        this.theme = theme
        this.dynamicColor = dynamicColor
        this.budgetAmount = budgetAmount
        this.budgetPeriod = budgetPeriod
        this.periodStart = periodStart
        this.loggedInAs = loggedInAs
    }
}


