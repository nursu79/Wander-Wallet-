package com.mobile.wanderwallet.data.remote

import com.mobile.wanderwallet.data.model.AvgSpending
import com.mobile.wanderwallet.data.model.BudgetComparisons
import com.mobile.wanderwallet.data.model.CreateExpenseRequest
import com.mobile.wanderwallet.data.model.ExpensePayload
import com.mobile.wanderwallet.data.model.ExpensesPayload
import com.mobile.wanderwallet.data.model.LoginRequest
import com.mobile.wanderwallet.data.model.MessagePayload
import com.mobile.wanderwallet.data.model.RefreshTokenRequest
import com.mobile.wanderwallet.data.model.SpendingByCategory
import com.mobile.wanderwallet.data.model.SpendingByMonth
import com.mobile.wanderwallet.data.model.TokensPayload
import com.mobile.wanderwallet.data.model.TotalSpending
import com.mobile.wanderwallet.data.model.TripPayload
import com.mobile.wanderwallet.data.model.TripsPayload
import com.mobile.wanderwallet.data.model.UserAndTokensPayload
import com.mobile.wanderwallet.data.model.UserPayload
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface WanderWalletApiService {
    // User
    @Multipart
    @POST("/register")
    suspend fun registerUser(
        @Part("username") username: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part avatar: MultipartBody.Part? = null
    ): UserAndTokensPayload

    @POST("/login")
    suspend fun loginUser(@Body requestBody: LoginRequest): TokensPayload

    @POST("/token")
    suspend fun refreshToken(@Body requestBody: RefreshTokenRequest): TokensPayload

    @GET("/profile")
    suspend fun getProfile(): UserPayload

    @Multipart
    @PUT("/profile")
    suspend fun updateProfile(
        @Part("username") username: RequestBody,
        @Part avatar: MultipartBody.Part? = null
    ): UserPayload

    @POST("/logout")
    suspend fun logoutUser(@Body requestBody: RefreshTokenRequest): MessagePayload

    // Trip
    @Multipart
    @POST("/trips")
    suspend fun createTrip(
        @Part("name") name: RequestBody,
        @Part("destination") destination: RequestBody,
        @Part("budget") budget: RequestBody,
        @Part("startDate") startDate: RequestBody,
        @Part("endDate") endDate: RequestBody,
        @Part tripImage: MultipartBody.Part? = null
    ): TripPayload

    @GET("/trips")
    suspend fun getTrips(): TripsPayload

    @GET("/pendingTrips")
    suspend fun getPendingTrips(): TripsPayload

    @GET("/currentTrips")
    suspend fun getCurrentTrips(): TripsPayload

    @GET("/pastTrips")
    suspend fun getPastTrips(): TripsPayload

    @GET("/trips/{id}")
    suspend fun getTrip(@Path("id") id: String): TripPayload

    @DELETE("/trips/{id}")
    suspend fun deleteTrip(@Path("id") id: String): MessagePayload

    @Multipart
    @POST("/trips/{id}")
    suspend fun updateTrip(
        @Path("id") id: String,
        @Part("name") name: RequestBody,
        @Part("destination") destination: RequestBody,
        @Part("budget") budget: RequestBody,
        @Part("startDate") startDate: RequestBody,
        @Part("endDate") endDate: RequestBody,
        @Part tripImage: MultipartBody.Part? = null
    ): TripPayload

    // Expense
    @POST("/trips/{tripId}/expenses")
    suspend fun createExpense(@Path("tripId") tripId: String, @Body requestBody: CreateExpenseRequest): ExpensePayload

    @GET("/trip/{tripId}/expenses")
    suspend fun getTripExpenses(@Path("tripId") tripId: String): ExpensesPayload

    @GET("/expenses/{id}")
    suspend fun getExpense(@Path("id") id: String): ExpensePayload

    @PUT("/expenses/{id}")
    suspend fun updateExpense(
        @Path("id") id: String,
        @Body requestBody: CreateExpenseRequest
    ): ExpensePayload

    @DELETE("/expenses/{id}")
    suspend fun deleteExpense(@Path("id") id: String): MessagePayload

    // Stats
    @GET("/stats/totalSpending")
    suspend fun getTotalSpending(): TotalSpending

    @GET("/stats/avgSpendingPerTrip")
    suspend fun getAvgSpendingPerTrip(): AvgSpending

    @GET("/stats/avgSpendingPerDay")
    suspend fun getAvgSpendingPerDay(): AvgSpending

    @GET("/stats/spendingByCategory")
    suspend fun getSpendingByCategory(): SpendingByCategory

    @GET("/stats/spendingByMonth")
    suspend fun getMonthlySpending(): SpendingByMonth

    @GET("/stats/budgetComparison")
    suspend fun getBudgetComparison(): BudgetComparisons

    @GET("/stats/mostExpensiveTrip")
    suspend fun getMostExpensiveTrip(): TripPayload

    @GET("/stats/leastExpensiveTrip")
    suspend fun getLeastExpensiveTrip(): TripPayload
}