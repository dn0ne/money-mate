package com.dn0ne.moneymate.app.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dn0ne.moneymate.MR
import com.dn0ne.moneymate.app.domain.Category
import com.dn0ne.moneymate.app.presentation.SpendingListEvent
import com.dn0ne.moneymate.app.presentation.SpendingListState
import com.dn0ne.moneymate.core.presentation.SimpleDialog
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun AddCategoryDialog(
    state: SpendingListState,
    newCategory: Category?,
    editing: Boolean,
    onEvent: (SpendingListEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    SimpleDialog(
        visible = state.showAddCategoryDialog,
        onDismissRequest = {
            onEvent(SpendingListEvent.DismissCategory)
        },
        modifier = modifier
            .heightIn(max = 350.dp)
    ) {
        var isInIconPickMode by remember {
            mutableStateOf(false)
        }
        AnimatedContent(
            targetState = isInIconPickMode
        ) { state ->
            when (state) {
                false -> {
                    Text(
                        text = stringResource(if (!editing) MR.strings.add_category else MR.strings.edit_category),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(16.dp)
                    )
                }

                true -> {
                    Text(
                        text = stringResource(MR.strings.pick_icon),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(16.dp)
                    )
                }
            }
        }

        IconPicker(
            newCategory = newCategory,
            onPick = { iconName ->
                onEvent(SpendingListEvent.OnPickedIconNameChanged(iconName))
            },
            onModeChange = {
                isInIconPickMode = it
            },
            error = state.categoryIconNameError?.let { stringResource(MR.strings.category_icon_error) },
            modifier = Modifier.fillMaxWidth()
        )

        var name by remember {
            mutableStateOf(newCategory?.name ?: "")
        }
        SpendingTextField(
            value = name,
            onValueChanged = {
                name = it
                onEvent(SpendingListEvent.OnNewCategoryNameChanged(name.trim()))
            },
            error = state.categoryNameError?.let { stringResource(MR.strings.category_name_error) },
            keyboardType = KeyboardType.Text,
            label = stringResource(MR.strings.name),
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp)
        )
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .padding(bottom = 16.dp)
        ) {
            TextButton(
                onClick = {
                    onEvent(SpendingListEvent.DismissCategory)
                }
            ) {
                Text(text = stringResource(MR.strings.cancel))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    onEvent(SpendingListEvent.SaveCategory)
                }
            ) {
                Text(text = stringResource(MR.strings.confirm))
            }
        }
    }
}