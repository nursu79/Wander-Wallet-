package com.mobile.wanderwallet.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Expense(
    val id: String,
    val name: String,
    val amount: Float,
    val category: Category,
    val date: Date,
    val createdAt: Date,
    val updatedAt: Date,
    val tripId: String,
    val notes: String?
)

enum class Category {
    FOOD,
    ACCOMMODATION,
    TRANSPORTATION,
    ENTERTAINMENT,
    SHOPPING,
    OTHER
}

data class CreateExpenseRequest(
    val name: String,
    val amount: String,
    val category: Category,
    val date: String,
    val notes: String?
)

data class Amount(
    val amount: Float
)

data class ExpenseByCategory(
    val category: Category,
    @SerializedName("_sum") val sum: Amount
)
