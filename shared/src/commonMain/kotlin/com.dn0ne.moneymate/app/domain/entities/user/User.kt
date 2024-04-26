package com.dn0ne.moneymate.app.domain.entities.user

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val email: String = "",
    val password: String = ""
)