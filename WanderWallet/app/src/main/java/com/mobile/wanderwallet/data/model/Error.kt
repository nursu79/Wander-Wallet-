package com.mobile.wanderwallet.data.model

data class MessageError(
    val message: String
)

data class UserError(
    val email: String? = null,
    val password: String? = null,
    val username: String? = null,
    val message: String? = null
)
