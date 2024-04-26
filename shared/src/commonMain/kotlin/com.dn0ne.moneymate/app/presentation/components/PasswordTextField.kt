package com.dn0ne.moneymate.app.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.dn0ne.moneymate.MR
import com.dn0ne.moneymate.app.domain.validators.UserValidator
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun PasswordTextField(
    value: String,
    onValueChanged: (String) -> Unit,
    maxLength: Int = 20,
    label: String? = null,
    placeholder: String? = null,
    error: UserValidator.ValidationError? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        var isPasswordVisible by remember {
            mutableStateOf(false)
        }
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
                    Text(
                        text = when(error) {
                            UserValidator.ValidationError.BlankPassword -> stringResource(MR.strings.blank_password)
                            UserValidator.ValidationError.IncorrectPassword -> stringResource(MR.strings.incorrect_password)
                            else -> ""
                        },
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            isError = error != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(
                    onClick = {
                        isPasswordVisible = !isPasswordVisible
                    }
                ) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                    )
                }
            }
        )
    }
}