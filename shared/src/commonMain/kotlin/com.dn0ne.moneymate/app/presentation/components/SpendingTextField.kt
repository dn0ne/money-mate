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
    error: String? = null,
    keyboardType: KeyboardType,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            label = label?.let {
                {
                    Text(text = label)
                }
            },
            value = value.take(maxLength),
            placeholder = placeholder?.let {
                {
                    Text(text = placeholder)
                }
            },
            onValueChange = {
                if (it.length <= maxLength) {
                    onValueChanged(it)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = error?.let {
                {
                    Text(text = error, color = MaterialTheme.colorScheme.error)
                }
            },
            isError = error != null,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )
    }
}