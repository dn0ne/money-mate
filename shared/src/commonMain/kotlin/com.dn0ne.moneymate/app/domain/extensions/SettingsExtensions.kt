package com.dn0ne.moneymate.app.domain.extensions

import com.dn0ne.moneymate.app.domain.enumerations.BudgetPeriod
import com.dn0ne.moneymate.app.domain.settings.Settings
import com.dn0ne.moneymate.app.domain.enumerations.Theme

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