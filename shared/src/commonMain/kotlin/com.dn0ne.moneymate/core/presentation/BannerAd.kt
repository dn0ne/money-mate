package com.dn0ne.moneymate.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun BannerAd(
    adId: String,
    modifier: Modifier = Modifier
)