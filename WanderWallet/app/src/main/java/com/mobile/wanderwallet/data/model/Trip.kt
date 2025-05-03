package com.mobile.wanderwallet.data.model

import java.util.Date

data class Trip(
    val id: String,
    val name: String,
    val destination: String,
    val budget: Float,
    val startDate: Date,
    val endDate: Date,
    val createdAt: Date,
    val updatedAt: Date,
    val userId: String,
    val imgUrl: String?,
    val expenses: List<Expense>?
)
