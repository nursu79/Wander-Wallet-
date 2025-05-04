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

data class TokensError(
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val message: String? = null
)

data class TripError(
    val name: String? = null,
    val destination: String? = null,
    val budget: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val message: String? = null
)

data class ExpenseError(
    val name: String? = null,
    val amount: String? = null,
    val category: String? = null,
    val date: String? = null,
    val message: String?= null
)
