package com.mobile.wanderwallet.data.model

import java.util.Date

data class User(
    val id: String,
    val username: String,
    val email: String,
    val createdAt: Date,
    val updatedAt: Date,
    val avatarUrl: String?
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RefreshTokenRequest(
    val refreshToken: String
)
