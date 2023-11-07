package com.dn0ne.moneymate.app.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun SpendingTextField(
    value: String,
    onValueChanged: (String) -> Unit,
    maxLength: Int = 20,
    label: String? = null,
    placeholder: String? = null,
    error: String?,
    keyboardType: KeyboardType,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            label = if (label != null) {
                {
                    Text(text = label)
                }
            } else null,
            value = value,
            placeholder = if (placeholder != null) {
                {
                    Text(text = placeholder)
                }
            } else null,
            onValueChange = {
                if (it.length <= maxLength) {
                    onValueChanged(it)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = if (error != null) {
                {
                    Text(text = error, color = MaterialTheme.colorScheme.error)
                }
            } else null,
            isError = error != null,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )
    }
}