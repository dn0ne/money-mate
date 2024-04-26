package com.dn0ne.moneymate.app.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.dn0ne.moneymate.app.domain.entities.spending.Category
import com.dn0ne.moneymate.app.domain.entities.spending.Spending
import com.dn0ne.moneymate.app.domain.entities.user.User
import com.dn0ne.moneymate.app.domain.extensions.copy
import com.dn0ne.moneymate.app.domain.repository.SpendingRepository
import com.dn0ne.moneymate.app.domain.settings.AppSettings
import com.dn0ne.moneymate.app.domain.sync.SyncService
import com.dn0ne.moneymate.app.domain.sync.SyncStatus
import com.dn0ne.moneymate.app.domain.validators.CategoryValidator
import com.dn0ne.moneymate.app.domain.validators.SpendingValidator
import com.dn0ne.moneymate.app.domain.validators.UserValidator
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.realm.kotlin.ext.toRealmList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * App view model class
 */
class SpendingListViewModel(
    private val repository: SpendingRepository,
    private val syncService: SyncService
) : ViewModel() {

    private val _appSettings = MutableStateFlow(AppSettings())
    private val _state = MutableStateFlow(SpendingListState())
    val state = combine(
        _state, repository.getSpendings(), repository.getCategories(), _appSettings
    ) { state, spendings, categories, settings ->
        state.copy(
            spendings = spendings,
            categories = categories,
            appSettings = settings,
            isDataLoaded = true
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), SpendingListState())

    init {
        _state.update {
            it.copy(
                isUserLoggedIn = syncService.isLoggedIn
            )
        }

        if (syncService.isLoggedIn) {
            viewModelScope.launch {
                delay(1000)
                startSync()
            }
        }
    }

    var newSpending: Spending? by mutableStateOf(null)
        private set

    var newCategory: Category? by mutableStateOf(null)
        private set

    var user: User? by mutableStateOf(null)
        private set

    fun onEvent(event: SpendingListEvent) {
        when (event) {
            SpendingListEvent.DeleteSpending -> {
                viewModelScope.launch {
                    _state.value.selectedSpending?.id?.let { id ->
                        _state.update {
                            it.copy(
                                isSelectedSpendingSheetOpen = false
                            )
                        }

                        repository.deleteSpending(id)
                        delay(300L) // Animation delay
                        _state.update {
                            it.copy(
                                selectedSpending = null
                            )
                        }
                    }
                }
            }

            SpendingListEvent.DismissSpending -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            isSelectedSpendingSheetOpen = false,
                            isAddSpendingSheetOpen = false,
                            areSpendingDetailsAdded = false,
                            categoryError = null,
                            amountError = null,
                            shortDescriptionError = null,
                            shoppingListError = null,
                        )
                    }

                    delay(300L) // Animation delay
                    newSpending = null
                }
            }

            is SpendingListEvent.EditSpending -> {
                _state.update {
                    it.copy(
                        isSelectedSpendingSheetOpen = false,
                        isAddSpendingSheetOpen = true,
                        areSpendingDetailsAdded = event.spending.shortDescription != null || event.spending.shoppingList.isNotEmpty(),
                        detailsType = if (event.spending.shoppingList.isNotEmpty()) 1 else 0
                    )
                }
                newSpending = event.spending
            }

            SpendingListEvent.OnAddNewSpendingClick -> {
                _state.update {
                    it.copy(
                        isAddSpendingSheetOpen = true
                    )
                }

                newSpending = Spending(
                    category = null, amount = 0f
                )
            }

            SpendingListEvent.OnAddDetailsClick -> {
                _state.update {
                    it.copy(
                        areSpendingDetailsAdded = !it.areSpendingDetailsAdded
                    )
                }
            }

            is SpendingListEvent.OnDetailsTypeChanged -> {
                _state.update {
                    it.copy(
                        detailsType = event.value
                    )
                }
            }


            is SpendingListEvent.OnAmountChanged -> {
                newSpending = newSpending?.copy(
                    amount = event.value
                )

                _state.value.amountError?.let {
                    _state.update {
                        it.copy(
                            amountError = null
                        )
                    }
                }
            }

            is SpendingListEvent.OnCategoryChanged -> {
                newSpending = newSpending?.copy(
                    category = event.value
                )

                _state.value.categoryError?.let {
                    _state.update {
                        it.copy(
                            categoryError = null
                        )
                    }
                }
            }

            is SpendingListEvent.OnShoppingListChanged -> {
                newSpending = newSpending?.copy(
                    shoppingList = event.list.toRealmList()
                )
            }

            is SpendingListEvent.OnShortDescriptionChanged -> {
                newSpending = newSpending?.copy(
                    shortDescription = event.value
                )

                _state.value.shortDescriptionError?.let {
                    _state.update {
                        it.copy(
                            shortDescriptionError = null
                        )
                    }
                }
            }

            SpendingListEvent.SaveSpending -> {
                newSpending?.let { spending ->
                    val result = SpendingValidator.validateSpending(spending)
                    val errors = listOfNotNull(
                        result.categoryError,
                        result.amountError,
                        result.shortDescriptionError,
                        result.shoppingListError
                    )

                    if (errors.isEmpty()) {
                        _state.update {
                            it.copy(
                                isAddSpendingSheetOpen = false,
                                areSpendingDetailsAdded = false,
                                categoryError = null,
                                amountError = null,
                                shortDescriptionError = null,
                                shoppingListError = null
                            )
                        }

                        viewModelScope.launch {
                            if (state.value.spendings.any { it.id == spending.id }) {
                                repository.updateSpending(spending)
                            } else {
                                repository.insertSpending(spending)
                            }
                            delay(300L) // Animation delay
                            newSpending = null
                        }
                    } else {
                        _state.update {
                            it.copy(
                                categoryError = result.categoryError,
                                amountError = result.amountError,
                                shortDescriptionError = result.shortDescriptionError,
                                shoppingListError = result.shoppingListError
                            )
                        }
                    }
                }
            }

            is SpendingListEvent.SelectSpending -> {
                _state.update {
                    it.copy(
                        selectedSpending = event.spending,
                        isSelectedSpendingSheetOpen = true
                    )
                }
            }

            SpendingListEvent.OnSummaryClick -> {
                _state.update {
                    it.copy(
                        isSummarySheetOpen = true
                    )
                }
            }

            SpendingListEvent.OnSummaryBackClick -> {
                _state.update {
                    it.copy(
                        isSummarySheetOpen = false
                    )
                }
            }

            SpendingListEvent.DeleteCategory -> {
                viewModelScope.launch {
                    newCategory?.id?.let { id ->
                        _state.update {
                            it.copy(
                                showAddCategoryDialog = false
                            )
                        }

                        repository.deleteCategory(id)
                        newCategory = null
                    }
                }
            }

            SpendingListEvent.DismissCategory -> {
                _state.update {
                    it.copy(
                        showAddCategoryDialog = false,
                        categoryNameError = null,
                        categoryIconNameError = null
                    )
                }
                newCategory = null
            }

            SpendingListEvent.OnAddNewCategoryClick -> {
                _state.update {
                    it.copy(
                        showAddCategoryDialog = true
                    )
                }

                newCategory = Category()
            }

            is SpendingListEvent.OnPickedIconNameChanged -> {
                newCategory = newCategory?.copy(
                    iconName = event.value
                )

                _state.value.categoryIconNameError?.let {
                    _state.update {
                        it.copy(
                            categoryIconNameError = null
                        )
                    }
                }
            }

            is SpendingListEvent.OnNewCategoryNameChanged -> {
                newCategory = newCategory?.copy(
                    name = event.value
                )

                _state.value.categoryNameError?.let {
                    _state.update {
                        it.copy(
                            categoryNameError = null
                        )
                    }
                }
            }

            is SpendingListEvent.SelectCategory -> {
                newCategory = event.category
            }

            SpendingListEvent.SaveCategory -> {
                newCategory?.let { category ->
                    val results = CategoryValidator.validateCategory(category)
                    val errors = listOfNotNull(
                        results.nameError,
                        results.iconNameError
                    )

                    if (errors.isEmpty()) {
                        _state.update {
                            it.copy(
                                showAddCategoryDialog = false,
                                categoryNameError = null,
                                categoryIconNameError = null
                            )
                        }

                        viewModelScope.launch {
                            if (state.value.categories.any { it.id == category.id }) {
                                repository.updateCategory(category)
                            } else {
                                repository.insertCategory(category)
                            }
                            newCategory = null
                        }

                    } else {
                        _state.update {
                            it.copy(
                                categoryNameError = results.nameError,
                                categoryIconNameError = results.iconNameError
                            )
                        }
                    }
                }
            }

            SpendingListEvent.OnSettingsClick -> {
                _state.update {
                    it.copy(
                        isSettingsSheetOpen = true
                    )
                }
            }

            SpendingListEvent.OnSettingsBackClick -> {
                _state.update {
                    it.copy(
                        isSettingsSheetOpen = false
                    )
                }
            }

            is SpendingListEvent.OnThemeChanged -> {
                _appSettings.update {
                    it.copy(
                        theme = event.theme
                    )
                }
            }

            is SpendingListEvent.OnDynamicColorChanged -> {
                _appSettings.update {
                    it.copy(
                        dynamicColor = event.dynamicColor
                    )
                }
            }

            SpendingListEvent.OnBudgetAmountChangeClick -> {
                _state.update {
                    it.copy(
                        showBudgetAmountChangeDialog = true
                    )
                }
            }

            SpendingListEvent.OnBudgetAmountChangeDismiss -> {
                _state.update {
                    it.copy(
                        showBudgetAmountChangeDialog = false,
                    )
                }
            }

            is SpendingListEvent.OnBudgetAmountChanged -> {
                _appSettings.update {
                    it.copy(
                        budgetAmount = event.value
                    )
                }

                _state.update {
                    it.copy(
                        showBudgetAmountChangeDialog = false,
                    )
                }
            }

            SpendingListEvent.OnBudgetPeriodChangeClick -> {
                _state.update {
                    it.copy(
                        showBudgetPeriodChangeDialog = true
                    )
                }
            }

            SpendingListEvent.OnBudgetPeriodChangeDismiss -> {
                _state.update {
                    it.copy(
                        showBudgetPeriodChangeDialog = false,
                    )
                }
            }

            is SpendingListEvent.OnBudgetPeriodChanged -> {
                _appSettings.update {
                    it.copy(
                        budgetPeriod = event.value
                    )
                }

                _state.update {
                    it.copy(
                        showBudgetPeriodChangeDialog = false
                    )
                }
            }

            SpendingListEvent.OnPeriodStartChangeClick -> {
                _state.update {
                    it.copy(
                        showPeriodStartChangeDialog = true
                    )
                }
            }

            SpendingListEvent.OnPeriodStartChangeDismiss -> {
                _state.update {
                    it.copy(
                        showPeriodStartChangeDialog = false
                    )
                }
            }

            is SpendingListEvent.OnBudgetPeriodStartChanged -> {
                _appSettings.update {
                    it.copy(
                        periodStart = event.value
                    )
                }

                _state.update {
                    it.copy(
                        showPeriodStartChangeDialog = false
                    )
                }
            }

            SpendingListEvent.OnAuthBackCLick -> {
                user = null
                _state.update {
                    it.copy(
                        isAuthSheetOpen = false,
                        emailError = null,
                        passwordError = null,
                        syncStatus = null
                    )
                }
            }

            SpendingListEvent.OnLoginClick -> {
                if (user == null) {
                    user = User()
                }
                _state.update {
                    it.copy(
                        isAuthSheetOpen = true,
                        isLoggingIn = true,
                        syncStatus = null
                    )
                }
            }

            SpendingListEvent.OnSignupClick -> {
                if (user == null) {
                    user = User()
                }
                _state.update {
                    it.copy(
                        isAuthSheetOpen = true,
                        isLoggingIn = false,
                        syncStatus = null
                    )
                }
            }

            SpendingListEvent.OnLogoutClick -> {
                syncService.forgetUser()
                _state.update {
                    it.copy(
                        isUserLoggedIn = syncService.isLoggedIn
                    )
                }
            }

            is SpendingListEvent.OnEmailChanged -> {
                user = user?.copy(
                    email = event.value
                )

                _state.value.emailError?.let {
                    _state.update {
                        it.copy(
                            emailError = null
                        )
                    }
                }
            }
            is SpendingListEvent.OnPasswordChanged -> {
                user = user?.copy(
                    password = event.value
                )

                _state.value.passwordError?.let {
                    _state.update {
                        it.copy(
                            passwordError = null
                        )
                    }
                }
            }

            SpendingListEvent.ConfirmLogin -> {
                user?.let { logInUser ->
                    val result = UserValidator.validateUser(logInUser)
                    val errors = listOfNotNull(
                        result.emailError,
                        result.passwordError
                    )

                    if (errors.isEmpty()) {
                        viewModelScope.launch {
                            _state.update {
                                it.copy(
                                    isAuthInProgress = true
                                )
                            }

                            val syncStatus = syncService.login(user)
                            syncStatus?.let { status ->
                                _state.update {
                                    it.copy(
                                        syncStatus = status,
                                        isAuthInProgress = false
                                    )
                                }
                            } ?: run {
                                _state.update {
                                    it.copy(
                                        isUserLoggedIn = syncService.isLoggedIn,
                                        isAuthSheetOpen = false,
                                        isAuthInProgress = false,
                                        syncStatus = null
                                    )
                                }

                                if (syncService.isLoggedIn) {
                                    _appSettings.update {
                                        it.copy(
                                            loggedInAs = logInUser.email
                                        )
                                    }

                                    startSync()
                                }

                                user = null
                            }

                        }
                    } else {
                        _state.update {
                            it.copy(
                                emailError = result.emailError,
                                passwordError = result.passwordError
                            )
                        }
                    }
                }

            }
            SpendingListEvent.ConfirmSignup -> {
                user?.let { signUpUser ->
                    val result = UserValidator.validateUser(signUpUser)
                    val errors = listOfNotNull(
                        result.emailError,
                        result.passwordError
                    )

                    if (errors.isEmpty()) {
                        viewModelScope.launch {
                            _state.update {
                                it.copy(
                                    isAuthInProgress = true
                                )
                            }

                            val syncStatus = syncService.signup(signUpUser)
                            syncStatus?.let { status ->
                                _state.update {
                                    it.copy(
                                        syncStatus = status,
                                        isAuthInProgress = false
                                    )
                                }
                            } ?: run {
                                _state.update {
                                    it.copy(
                                        isUserLoggedIn = syncService.isLoggedIn,
                                        isAuthSheetOpen = false,
                                        isAuthInProgress = false,
                                        syncStatus = null
                                    )
                                }

                                if (syncService.isLoggedIn) {
                                    _appSettings.update {
                                        it.copy(
                                            loggedInAs = signUpUser.email
                                        )
                                    }

                                    startSync()
                                }

                                user = null
                            }
                        }
                    } else {
                        _state.update {
                            it.copy(
                                emailError = result.emailError,
                                passwordError = result.passwordError
                            )
                        }
                    }
                }
            }

            SpendingListEvent.StartSync -> {
                startSync()
            }
        }
    }

    override fun onCleared() {
        viewModelScope.launch {
            startSync()
            super.onCleared()
        }
    }

    private fun startSync() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isSyncingInProgress = true,
                    isSyncEndedWithError = null,
                    syncStatus = SyncStatus.InProgress
                )
            }

            val error = syncService.syncChanges()

            error?.let { status ->
                _state.update {
                    it.copy(
                        syncStatus = status,
                        isSyncEndedWithError = true
                    )
                }
            } ?: run {
                _state.update {
                    it.copy(
                        syncStatus = SyncStatus.Success(),
                        isSyncEndedWithError = false
                    )
                }
                delay(1000)

                _state.update {
                    it.copy(
                        isSyncingInProgress = false
                    )
                }
            }
        }
    }
}