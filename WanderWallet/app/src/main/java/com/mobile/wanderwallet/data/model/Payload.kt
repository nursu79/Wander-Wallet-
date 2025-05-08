package com.mobile.wanderwallet.data.model

data class UserPayload(
    val user: User
)

data class MessagePayload(
    val message: String
)

data class TokensPayload(
    val accessToken: String,
    val refreshToken: String
)

data class UserAndTokensPayload(
    val user: User,
    val accessToken: String,
    val refreshToken: String
)

data class TripPayload(
    val trip: Trip?,
    val totalExpenditure: Float? = 0f,
    val expensesByCategory: List<ExpenseByCategory>
)

data class TripsPayload(
    val trips: List<Trip>
)

data class ExpensePayload(
    val expense: Expense
)

data class ExpensesPayload(
    val expenses: List<Expense>
)

data class NotificationsPayload(
    val notifications: List<Notification>
)

data class TotalSpending(
    val totalSpending: Float,
    val totalBudget: Float
)

data class AvgSpending(
    val avgSpending: Float
)

data class SpendingByCategory(
    val categories: List<CategorySpending>
)

data class CategorySpending(
    val category: Category,
    val amount: Float
)

data class SpendingByMonth(
    val expensesByMonth: List<Map<String, Float>>
)

data class BudgetComparison(
    val tripId: String,
    val name: String,
    val budget: Float,
    val expenditure: Float
)

data class BudgetComparisons(
    val budgetComparison: List<BudgetComparison>
)

data class AllStats(
    val totalSpending: Float,
    val totalBudget: Float,
    val avgSpendingPerTrip: Float,
    val avgSpendingPerDay: Float,
    val spendingByCategory: List<CategorySpending>,
    val monthlySpending: List<Map<String, Float>>,
    val budgetComparisons: List<BudgetComparison>,
    val mostExpensiveTrip: TripPayload,
    val leastExpensiveTrip: TripPayload
)

sealed class Result<out T, out E> {
    data class Success<out T>(val data: T): Result<T, Nothing>()
    data class Error<E>(val error: E, val loggedOut: Boolean = false): Result<Nothing, E>()
}
