package com.dn0ne.moneymate.core.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dn0ne.moneymate.MR
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.datetime.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleDateRangePicker(
    visible: Boolean,
    state: DateRangePickerState,
    onDismissRequest: () -> Unit,
    onCancelClick: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentEpochMillis by remember {
        mutableStateOf(Clock.System.now().toEpochMilliseconds())
    }
    if (visible) {
        DatePickerDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {},
            modifier = modifier
        ) {
            DateRangePicker(
                state = state,
                dateValidator = { timeInMillis ->
                    timeInMillis <= currentEpochMillis
                },
                title = {
                    Text(
                        text = stringResource(MR.strings.pick_date_range),
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .padding(top = 16.dp, bottom = 8.dp)
                    )
                },
                headline = {
                    DateRangePickerDefaults.DateRangePickerHeadline(
                        state = state,
                        dateFormatter = DatePickerFormatter(),
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .padding(bottom = 16.dp)
                    )
                },
                showModeToggle = false,
                modifier = Modifier.fillMaxHeight(.8f)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp)
            ) {
                TextButton(
                    onClick = onCancelClick
                ) {
                    Text(text = stringResource(MR.strings.cancel))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = onConfirmClick
                ) {
                    Text(text = stringResource(MR.strings.confirm))
                }
            }
        }
    }
}