package com.dn0ne.moneymate.core.presentation

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler

@Composable
fun UriTextButton(
    text: String,
    uri: String,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    TextButton(
        onClick = {
            uriHandler.openUri(uri)
        },
        modifier = modifier
    ) {
        Text(
            text = text
        )
    }
}