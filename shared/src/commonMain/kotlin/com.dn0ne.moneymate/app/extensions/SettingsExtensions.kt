package com.dn0ne.moneymate.app.extensions

import com.dn0ne.moneymate.app.domain.BudgetPeriod
import com.dn0ne.moneymate.app.domain.Settings
import com.dn0ne.moneymate.app.domain.Theme

fun Settings.copy(
    theme: Theme = this.theme,
    dynamicColor: Boolean? = this.dynamicColor,
    budgetAmount: Float = this.budgetAmount,
    budgetPeriod: BudgetPeriod = this.budgetPeriod,
    periodStart: Int = this.periodStart
): Settings {
    return Settings(
        theme = theme,
        dynamicColor = dynamicColor,
        budgetAmount = budgetAmount,
        budgetPeriod = budgetPeriod,
        periodStart = periodStart
    )
}