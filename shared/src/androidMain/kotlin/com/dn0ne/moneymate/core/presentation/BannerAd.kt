package com.dn0ne.moneymate.core.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
actual fun BannerAd(
    adId: String,
    modifier: Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        AndroidView(
            modifier = Modifier,
                //.clip(RoundedCornerShape(8.dp)),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    adUnitId = adId
                    // Request Ad
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}