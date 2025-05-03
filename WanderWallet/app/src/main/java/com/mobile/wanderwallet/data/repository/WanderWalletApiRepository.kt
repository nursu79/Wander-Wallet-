package com.mobile.wanderwallet.data.repository

import android.content.ContentResolver
import android.net.Uri
import com.mobile.wanderwallet.data.model.AllStats
import com.mobile.wanderwallet.data.model.Category
import com.mobile.wanderwallet.data.model.CreateExpenseRequest
import com.mobile.wanderwallet.data.model.ExpensePayload
import com.mobile.wanderwallet.data.model.ExpensesPayload
import com.mobile.wanderwallet.data.model.LoginRequest
import com.mobile.wanderwallet.data.model.MessagePayload
import com.mobile.wanderwallet.data.model.RefreshTokenRequest
import com.mobile.wanderwallet.data.model.TokensPayload
import com.mobile.wanderwallet.data.model.TripPayload
import com.mobile.wanderwallet.data.model.TripsPayload
import com.mobile.wanderwallet.data.model.UserAndTokensPayload
import com.mobile.wanderwallet.data.model.UserPayload
import com.mobile.wanderwallet.data.remote.WanderWalletApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.Date

interface WanderWalletApiRepository {

    // User
    suspend fun registerUser(email: String, username: String, password: String, avatarUri: Uri? = null, contentResolver: ContentResolver? = null): UserAndTokensPayload

    suspend fun loginUser(email: String, password: String): TokensPayload

    suspend fun refreshToken(): TokensPayload

    suspend fun getProfile(): UserPayload

    suspend fun updateProfile(username: String, avatarUri: Uri? = null, contentResolver: ContentResolver? = null): UserPayload

    suspend fun logoutUser(): MessagePayload


    // Trip
    suspend fun createTrip(name: String, destination: String, budget: String, startDate: Date, endDate: Date, tripImageUri: Uri? = null, contentResolver: ContentResolver? = null): TripPayload

    suspend fun getTrips(): TripsPayload

    suspend fun getPendingTrips(): TripsPayload

    suspend fun getCurrentTrips(): TripsPayload

    suspend fun getPastTrips(): TripsPayload

    suspend fun getTrip(id: String): TripPayload

    suspend fun deleteTrip(id: String): MessagePayload

    suspend fun updateTrip(id: String, name: String, destination: String, budget: String, startDate: Date, endDate: Date, tripImageUri: Uri? = null, contentResolver: ContentResolver? = null): TripPayload

    // Expenses
    suspend fun createExpense(tripId: String, name: String, amount: String, category: Category, date: Date, notes: String?): ExpensePayload

    suspend fun getTripExpenses(tripId: String): ExpensesPayload

    suspend fun getExpense(id: String): ExpensePayload

    suspend fun updateExpense(id: String, name: String, amount: String, category: Category, date: Date, notes: String?): ExpensePayload

    suspend fun deleteExpense(id: String): MessagePayload

    // Stats
    suspend fun getStatistics(): AllStats
}

class NetworkWanderWalletApiRepository(
    private val wanderWalletApiService: WanderWalletApiService,
    private val tokenProvider: TokenProvider
): WanderWalletApiRepository {

    // User

    override suspend fun registerUser(email: String, username: String, password: String, avatarUri: Uri?, contentResolver: ContentResolver?): UserAndTokensPayload {
        val emailPart = email.toRequestBody("text/plain".toMediaType())
        val usernamePart = username.toRequestBody("text/plain".toMediaType())
        val passwordPart = password.toRequestBody("text/plain".toMediaType())
        if (avatarUri != null && contentResolver != null) {
            val inputStream = contentResolver.openInputStream(avatarUri)
            val fileName = avatarUri.lastPathSegment ?: "avatar.jpg"
            val imageBytes = inputStream?.readBytes() ?: throw Error()
            val avatarPart = MultipartBody.Part.createFormData(
                "avatar",
                fileName,
                imageBytes.toRequestBody("image/*".toMediaTypeOrNull())
            )

            return wanderWalletApiService.registerUser(
                username = usernamePart,
                email = emailPart,
                password = passwordPart,
                avatar = avatarPart
            )
        }

        val response =  wanderWalletApiService.registerUser(
            username = usernamePart,
            email = emailPart,
            password = passwordPart
        )

        tokenProvider.setAccessToken(response.accessToken)
        tokenProvider.setRefreshToken(response.refreshToken)
        return response
    }

    override suspend fun loginUser(email: String, password: String): TokensPayload {
        val requestBody = LoginRequest(email, password)

        val response = wanderWalletApiService.loginUser(requestBody)
        tokenProvider.setAccessToken(response.accessToken)
        tokenProvider.setRefreshToken(response.refreshToken)
        return response
    }

    override suspend fun refreshToken(): TokensPayload {
        val refreshToken = tokenProvider.getRefreshToken()
        val refreshTokenRequest = RefreshTokenRequest(refreshToken ?: "")
        val response = wanderWalletApiService.refreshToken(refreshTokenRequest)

        tokenProvider.setAccessToken(response.accessToken)
        tokenProvider.setRefreshToken(response.refreshToken)
        return response
    }

    override suspend fun getProfile(): UserPayload {
        return wanderWalletApiService.getProfile()
    }

    override suspend fun updateProfile(username: String, avatarUri: Uri?, contentResolver: ContentResolver?): UserPayload {
        val usernamePart = username.toRequestBody("text/plain".toMediaType())

        if (avatarUri != null && contentResolver != null) {
            val inputStream = contentResolver.openInputStream(avatarUri)
            val fileName = avatarUri.lastPathSegment ?: "avatar.jpg"
            val imageBytes = inputStream?.readBytes() ?: throw Error()
            val avatarPart = MultipartBody.Part.createFormData(
                "avatar",
                fileName,
                imageBytes.toRequestBody("image/*".toMediaTypeOrNull())
            )

            return wanderWalletApiService.updateProfile(usernamePart, avatarPart)
        }

        return wanderWalletApiService.updateProfile(usernamePart)
    }

    override suspend fun logoutUser(): MessagePayload {
        val refreshToken = tokenProvider.getRefreshToken()
        val refreshTokenRequest = RefreshTokenRequest(refreshToken ?: "")
        return wanderWalletApiService.logoutUser(refreshTokenRequest)
    }

    // Trip
    override suspend fun createTrip(
        name: String,
        destination: String,
        budget: String,
        startDate: Date,
        endDate: Date,
        tripImageUri: Uri?,
        contentResolver: ContentResolver?
    ): TripPayload {
        val namePart = name.toRequestBody("text/plain".toMediaType())
        val destinationPart = destination.toRequestBody("text/plain".toMediaType())
        val budgetPart = budget.toRequestBody("text/plain".toMediaType())
        val startDatePart = startDate.toString().toRequestBody("text/plain".toMediaType())
        val endDatePart = endDate.toString().toRequestBody("text/plain".toMediaType())

        if (tripImageUri != null && contentResolver != null) {
            val inputStream = contentResolver.openInputStream(tripImageUri)
            val fileName = tripImageUri.lastPathSegment ?: "avatar.jpg"
            val imageBytes = inputStream?.readBytes() ?: throw Error()
            val tripImagePart = MultipartBody.Part.createFormData(
                "avatar",
                fileName,
                imageBytes.toRequestBody("image/*".toMediaTypeOrNull())
            )

            return wanderWalletApiService.createTrip(
                name = namePart,
                destination = destinationPart,
                budget = budgetPart,
                startDate = startDatePart,
                endDate = endDatePart,
                tripImage = tripImagePart
            );
        }

        return wanderWalletApiService.createTrip(
            name = namePart,
            destination = destinationPart,
            budget = budgetPart,
            startDate = startDatePart,
            endDate = endDatePart
        )
    }

    override suspend fun getTrips(): TripsPayload {
        return wanderWalletApiService.getTrips()
    }

    override suspend fun getPendingTrips(): TripsPayload {
        return wanderWalletApiService.getPendingTrips()
    }

    override suspend fun getCurrentTrips(): TripsPayload {
        return wanderWalletApiService.getCurrentTrips()
    }

    override suspend fun getPastTrips(): TripsPayload {
        return wanderWalletApiService.getPastTrips()
    }

    override suspend fun getTrip(id: String): TripPayload {
        return wanderWalletApiService.getTrip(id)
    }

    override suspend fun deleteTrip(id: String): MessagePayload {
        return wanderWalletApiService.deleteTrip(id)
    }

    override suspend fun updateTrip(
        id: String,
        name: String,
        destination: String,
        budget: String,
        startDate: Date,
        endDate: Date,
        tripImageUri: Uri?,
        contentResolver: ContentResolver?
    ): TripPayload {
        val namePart = name.toRequestBody("text/plain".toMediaType())
        val destinationPart = destination.toRequestBody("text/plain".toMediaType())
        val budgetPart = budget.toRequestBody("text/plain".toMediaType())
        val startDatePart = startDate.toString().toRequestBody("text/plain".toMediaType())
        val endDatePart = endDate.toString().toRequestBody("text/plain".toMediaType())

        if (tripImageUri != null && contentResolver != null) {
            val inputStream = contentResolver.openInputStream(tripImageUri)
            val fileName = tripImageUri.lastPathSegment ?: "avatar.jpg"
            val imageBytes = inputStream?.readBytes() ?: throw Error()
            val tripImagePart = MultipartBody.Part.createFormData(
                "avatar",
                fileName,
                imageBytes.toRequestBody("image/*".toMediaTypeOrNull())
            )

            return wanderWalletApiService.updateTrip(
                id = id,
                name = namePart,
                destination = destinationPart,
                budget = budgetPart,
                startDate = startDatePart,
                endDate = endDatePart,
                tripImage = tripImagePart
            )
        }

        return wanderWalletApiService.updateTrip(
            id = id,
            name = namePart,
            destination = destinationPart,
            budget = budgetPart,
            startDate = startDatePart,
            endDate = endDatePart
        )
    }

    // Expense

    override suspend fun createExpense(
        tripId: String,
        name: String,
        amount: String,
        category: Category,
        date: Date,
        notes: String?
    ): ExpensePayload {
        val requestBody = CreateExpenseRequest(
            name = name,
            amount = amount,
            category = category,
            date = date,
            notes = notes
        );

        return wanderWalletApiService.createExpense(tripId, requestBody)
    }

    override suspend fun getTripExpenses(tripId: String): ExpensesPayload {
        return wanderWalletApiService.getTripExpenses(tripId)
    }

    override suspend fun getExpense(id: String): ExpensePayload {
        return wanderWalletApiService.getExpense(id)
    }

    override suspend fun updateExpense(
        id: String,
        name: String,
        amount: String,
        category: Category,
        date: Date,
        notes: String?
    ): ExpensePayload {
        val requestBody = CreateExpenseRequest(
            name = name,
            amount = amount,
            category = category,
            date = date,
            notes = notes
        )

        return wanderWalletApiService.updateExpense(id, requestBody)
    }

    override suspend fun deleteExpense(id: String): MessagePayload {
        return wanderWalletApiService.deleteExpense(id)
    }

    override suspend fun getStatistics(): AllStats {
        val totalSpending = wanderWalletApiService.getTotalSpending()
        val avgSpendingPerTrip = wanderWalletApiService.getAvgSpendingPerTrip()
        val avgSpendingPerDay = wanderWalletApiService.getAvgSpendingPerDay()
        val spendingByCategory = wanderWalletApiService.getSpendingByCategory()
        val spendingByMonth = wanderWalletApiService.getMonthlySpending()
        val budgetComparison = wanderWalletApiService.getBudgetComparison()
        val mostExpensiveTrip = wanderWalletApiService.getMostExpensiveTrip()
        val leastExpensiveTrip = wanderWalletApiService.getLeastExpensiveTrip()

        return AllStats(
            totalSpending = totalSpending.totalSpending,
            totalBudget = totalSpending.totalBudget,
            avgSpendingPerTrip = avgSpendingPerTrip.avgSpending,
            avgSpendingPerDay = avgSpendingPerDay.avgSpending,
            spendingByCategory = spendingByCategory.categories,
            monthlySpending = spendingByMonth.expensesByMonth,
            budgetComparisons = budgetComparison.budgetComparison,
            mostExpensiveTrip = mostExpensiveTrip,
            leastExpensiveTrip = leastExpensiveTrip
        )
    }
}
