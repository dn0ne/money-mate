package com.dn0ne.moneymate.app.domain.enumerations

import androidx.compose.runtime.Composable
import com.dn0ne.moneymate.MR
import dev.icerock.moko.resources.compose.stringResource

enum class BudgetPeriod {
    MONTH, WEEK, DAY;

    val localizedName: String
    @Composable get() {
        return stringResource(
            when(this) {
                MONTH -> MR.strings.month
                WEEK -> MR.strings.week
                DAY -> MR.strings.day
            }
        )
    }
}