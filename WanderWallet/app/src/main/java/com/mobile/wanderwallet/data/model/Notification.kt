package com.mobile.wanderwallet.data.model

data class Notification(
    val id: String,
    val userId: String,
    val user: User?,
    val tripId: String,
    val trip: Trip?
)