package com.dn0ne.moneymate.app.domain.extensions

import com.dn0ne.moneymate.app.domain.enumerations.BudgetPeriod
import com.dn0ne.moneymate.app.domain.enumerations.Theme
import com.dn0ne.moneymate.app.domain.settings.AppSettings

fun AppSettings.copy(
    theme: Theme = this.theme,
    dynamicColor: Boolean? = this.dynamicColor,
    budgetAmount: Float = this.budgetAmount,
    budgetPeriod: BudgetPeriod = this.budgetPeriod,
    periodStart: Int = this.periodStart,
    loggedInAs: String? = this.loggedInAs
): AppSettings {
    return AppSettings(
        theme = theme,
        dynamicColor = dynamicColor,
        budgetAmount = budgetAmount,
        budgetPeriod = budgetPeriod,
        periodStart = periodStart,
        loggedInAs = loggedInAs
    )
}