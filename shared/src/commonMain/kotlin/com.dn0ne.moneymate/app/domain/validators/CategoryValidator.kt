package com.dn0ne.moneymate.app.domain.validators

import com.dn0ne.moneymate.app.domain.entities.Category

object CategoryValidator {

    fun validateCategory(category: Category): ValidationResult {
        var result = ValidationResult()

        if (category.name.any { !it.isDigit() && !it.isLetter() && !it.isWhitespace() } || category.name.isBlank()) {
            result = result.copy(
                nameError = true
            )
        }

        if (category.iconName.isBlank()) {
            result = result.copy(
                iconNameError = true
            )
        }

        return result
    }

    data class ValidationResult(
        val nameError: Boolean? = null,
        val iconNameError: Boolean? = null
    )
}