package com.dn0ne.moneymate.app.domain.validators

import com.dn0ne.moneymate.app.domain.entities.user.User

object UserValidator {
    private const val EMAIL_REGEX_STRING = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}\$"
    private const val PASSWORD_REGEX_STRING = "^[^ ]{8,20}\$"

    fun validateUser(user: User): ValidationResult {
        var result = ValidationResult()

        if (user.email.isBlank()) {
            result = result.copy(
                emailError = ValidationError.BlankEmail
            )
        }
        else if (!user.email.matches(EMAIL_REGEX_STRING.toRegex())) {
            result = result.copy(
                emailError = ValidationError.IncorrectEmail
            )
        }

        if (user.password.isBlank()) {
            result = result.copy(
                passwordError = ValidationError.BlankPassword
            )
        }
        else if (!user.password.matches(PASSWORD_REGEX_STRING.toRegex())) {
            result = result.copy(
                passwordError = ValidationError.IncorrectPassword
            )
        }

        return result
    }

    data class ValidationResult(
        val emailError: ValidationError? = null,
        val passwordError: ValidationError? = null
    )

    sealed class ValidationError {
        data object BlankEmail: ValidationError()
        data object IncorrectEmail: ValidationError()
        data object BlankPassword: ValidationError()
        data object IncorrectPassword: ValidationError()
    }
}