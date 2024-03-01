package com.dn0ne.moneymate.app.domain.enumerations

import androidx.compose.runtime.Composable
import com.dn0ne.moneymate.MR
import dev.icerock.moko.resources.compose.stringResource

enum class Theme {
    SYSTEM, LIGHT, DARK;

    val localizedName: String
        @Composable get() {
            return stringResource(
                when (this) {
                    SYSTEM -> MR.strings.system
                    LIGHT -> MR.strings.light
                    DARK -> MR.strings.dark
                }
            )
        }
}