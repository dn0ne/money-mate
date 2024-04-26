package com.dn0ne.moneymate.app.presentation.sheets

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIos
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dn0ne.moneymate.MR
import com.dn0ne.moneymate.app.domain.entities.user.User
import com.dn0ne.moneymate.app.domain.extensions.safeSystemBarsAndDisplayCutoutPadding
import com.dn0ne.moneymate.app.domain.sync.SyncStatus
import com.dn0ne.moneymate.app.presentation.SpendingListEvent
import com.dn0ne.moneymate.app.presentation.SpendingListState
import com.dn0ne.moneymate.app.presentation.components.CollapsingTopAppBar
import com.dn0ne.moneymate.app.presentation.components.EmailTextField
import com.dn0ne.moneymate.app.presentation.components.PasswordTextField
import com.dn0ne.moneymate.core.presentation.BackGestureHandler
import com.dn0ne.moneymate.core.presentation.SimpleBottomSheet
import dev.icerock.moko.resources.compose.stringResource
import kotlin.math.roundToInt

@Composable
fun AuthSheet(
    state: SpendingListState,
    user: User?,
    onEvent: (SpendingListEvent) -> Unit,
    isOpen: Boolean,
    modifier: Modifier = Modifier,
    maxContentWidth: Dp = Dp.Unspecified
) {
    SimpleBottomSheet(
        visible = isOpen,
        enableSafePadding = false,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (!state.isAuthInProgress) {
                BackGestureHandler {
                    onEvent(SpendingListEvent.OnAuthBackCLick)
                }
            }

            Column(
                modifier = Modifier.safeSystemBarsAndDisplayCutoutPadding()
            ) {
                var isTopBarCollapsed by remember {
                    mutableStateOf(false)
                }
                CollapsingTopAppBar(
                    isCollapsed = isTopBarCollapsed,
                    title = {
                        AnimatedContent(
                            targetState = if (state.isLoggingIn) {
                                stringResource(MR.strings.log_in)
                            } else stringResource(MR.strings.sign_up)
                        ) {
                            Text(text = it, style = MaterialTheme.typography.headlineMedium)
                        }
                    },
                    collapsedTitle = {
                        AnimatedContent(
                            targetState = if (state.isLoggingIn) {
                                stringResource(MR.strings.log_in)
                            } else stringResource(MR.strings.sign_up)
                        ) {
                            Text(text = it, style = MaterialTheme.typography.titleMedium)
                        }
                    },
                    leadingButton = {
                        IconButton(
                            onClick = {
                                onEvent(SpendingListEvent.OnAuthBackCLick)
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBackIos,
                                contentDescription = stringResource(MR.strings.add_spending_sheet_close_description),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    modifier = Modifier.widthIn(max = maxContentWidth)
                )


                val scrollState = rememberScrollState()
                var isScrollingUp by remember {
                    mutableStateOf(true)
                }

                val scrollableState = rememberScrollableState {
                    if (it.roundToInt() > 5) {
                        isScrollingUp = true
                    } else if (it.roundToInt() < -5 || scrollState.canScrollBackward) {
                        isScrollingUp = false
                    }
                    it
                }

                if (!scrollState.canScrollBackward && isScrollingUp) {
                    isTopBarCollapsed = false
                } else if (!isScrollingUp) {
                    isTopBarCollapsed = true
                }

                Column(
                    modifier = Modifier
                        .widthIn(max = maxContentWidth)
                        .fillMaxSize()
                        .scrollable(scrollableState, Orientation.Vertical)
                        .verticalScroll(scrollState)
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    var email by remember {
                        mutableStateOf(user?.email ?: "")
                    }
                    EmailTextField(
                        label = stringResource(MR.strings.email),
                        value = email,
                        onValueChanged = {
                            email = it
                            onEvent(SpendingListEvent.OnEmailChanged(email))
                        },
                        error = state.emailError
                    )

                    var password by remember {
                        mutableStateOf(user?.email ?: "")
                    }
                    PasswordTextField(
                        label = stringResource(MR.strings.password),
                        value = password,
                        onValueChanged = {
                            password = it
                            onEvent(SpendingListEvent.OnPasswordChanged(password))
                        },
                        error = state.passwordError
                    )

                    AnimatedVisibility(
                        visible = state.syncStatus != null,
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Text(
                            text = when (state.syncStatus) {
                                SyncStatus.Timeout -> stringResource(MR.strings.sync_timeout)
                                SyncStatus.NoNetwork -> stringResource(MR.strings.sync_no_network)
                                SyncStatus.IncorrectUsernameOrPassword -> stringResource(MR.strings.sync_incorrect_username_or_password)
                                else -> ""
                            },
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FilledTonalButton(
                            onClick = {
                                if (state.isLoggingIn) {
                                    onEvent(SpendingListEvent.OnSignupClick)
                                } else {
                                    onEvent(SpendingListEvent.OnLoginClick)
                                }
                            },
                            modifier = Modifier.fillMaxWidth().weight(1f)
                        ) {
                            AnimatedContent(
                                targetState = state.isLoggingIn
                            ) { isLoggingIn ->
                                if (isLoggingIn) {
                                    Text(text = stringResource(MR.strings.sign_up))
                                } else {
                                    Text(text = stringResource(MR.strings.log_in))
                                }
                            }
                        }

                        Button(
                            onClick = {
                                if (state.isLoggingIn) {
                                    onEvent(SpendingListEvent.ConfirmLogin)
                                } else {
                                    onEvent(SpendingListEvent.ConfirmSignup)
                                }
                            },
                            modifier = Modifier.fillMaxWidth().weight(1f)
                        ) {
                            Text(text = stringResource(MR.strings.confirm))
                        }
                    }
                }
            }

            Column(modifier = Modifier.fillMaxSize()) {
                AnimatedVisibility(
                    visible = state.isAuthInProgress,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = MaterialTheme.colorScheme.scrim.copy(alpha = .7f))
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = if (state.isLoggingIn) stringResource(MR.strings.logging_in) else stringResource(MR.strings.signing_up),
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.surface
                        )
                    }
                }
            }
        }
    }
}