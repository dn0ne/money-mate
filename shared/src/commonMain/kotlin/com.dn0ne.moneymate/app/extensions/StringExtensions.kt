package com.dn0ne.moneymate.app.extensions

/**
 * Returns string where first character is uppercase and others are lowercase.
 * Use only for words.
 */
fun String.capitalize(): String {
    return this.lowercase().replaceFirstChar { it.uppercase() }
}