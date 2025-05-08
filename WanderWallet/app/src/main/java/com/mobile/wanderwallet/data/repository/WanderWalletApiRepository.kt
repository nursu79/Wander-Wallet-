package com.mobile.wanderwallet.data.repository

import android.content.ContentResolver
import android.net.Uri
import com.google.gson.Gson
import com.mobile.wanderwallet.data.model.AllStats
import com.mobile.wanderwallet.data.model.Category
import com.mobile.wanderwallet.data.model.CreateExpenseRequest
import com.mobile.wanderwallet.data.model.ExpenseError
import com.mobile.wanderwallet.data.model.ExpensePayload
import com.mobile.wanderwallet.data.model.ExpensesPayload
import com.mobile.wanderwallet.data.model.LoginRequest
import com.mobile.wanderwallet.data.model.MessageError
import com.mobile.wanderwallet.data.model.MessagePayload
import com.mobile.wanderwallet.data.model.NotificationsPayload
import com.mobile.wanderwallet.data.model.RefreshTokenRequest
import com.mobile.wanderwallet.data.model.Result
import com.mobile.wanderwallet.data.model.TokensError
import com.mobile.wanderwallet.data.model.TokensPayload
import com.mobile.wanderwallet.data.model.TripError
import com.mobile.wanderwallet.data.model.TripPayload
import com.mobile.wanderwallet.data.model.TripsPayload
import com.mobile.wanderwallet.data.model.UserAndTokensPayload
import com.mobile.wanderwallet.data.model.UserError
import com.mobile.wanderwallet.data.model.UserPayload
import com.mobile.wanderwallet.data.remote.WanderWalletApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.IOException

interface WanderWalletApiRepository {

    // User
    suspend fun registerUser(email: String, username: String, password: String, avatarUri: Uri? = null, contentResolver: ContentResolver? = null): Result<UserAndTokensPayload, UserError>

    suspend fun loginUser(email: String, password: String): Result<TokensPayload, UserError>

    suspend fun refreshToken(): Result<TokensPayload, MessageError>

    suspend fun getProfile(retry: Boolean = true): Result<UserPayload, MessageError>

    suspend fun updateProfile(username: String, avatarUri: Uri? = null, contentResolver: ContentResolver? = null, retry: Boolean = true): Result<UserPayload, UserError>

    suspend fun getNotifications(retry: Boolean = true): Result<NotificationsPayload, MessageError>

    suspend fun logoutUser(retry: Boolean = true): Result<MessagePayload, TokensError>

    // Trip
    suspend fun createTrip(name: String, destination: String, budget: String, startDate: String, endDate: String, tripImageUri: Uri? = null, contentResolver: ContentResolver? = null, retry: Boolean = true): Result<TripPayload, TripError>

    suspend fun getTrips(retry: Boolean = true): Result<TripsPayload, MessageError>

    suspend fun getPendingTrips(retry: Boolean = true): Result<TripsPayload, MessageError>

    suspend fun getCurrentTrips(retry: Boolean = true): Result<TripsPayload, MessageError>

    suspend fun getPastTrips(retry: Boolean = true): Result<TripsPayload, MessageError>

    suspend fun getTrip(id: String, retry: Boolean = true): Result<TripPayload, MessageError>

    suspend fun deleteTrip(id: String, retry: Boolean = true): Result<MessagePayload, MessageError>

    suspend fun updateTrip(id: String, name: String, destination: String, budget: String, startDate: String, endDate: String, tripImageUri: Uri? = null, contentResolver: ContentResolver? = null, retry: Boolean = true): Result<TripPayload, TripError>

    // Expenses
    suspend fun createExpense(tripId: String, name: String, amount: String, category: Category, date: String, notes: String?, retry: Boolean = true): Result<ExpensePayload, ExpenseError>

    suspend fun getTripExpenses(tripId: String, retry: Boolean = true): Result<ExpensesPayload, MessageError>

    suspend fun getExpense(id: String, retry: Boolean = true): Result<ExpensePayload, MessageError>

    suspend fun updateExpense(id: String, name: String, amount: String, category: Category, date: String, notes: String?, retry: Boolean = true): Result<ExpensePayload, ExpenseError>

    suspend fun deleteExpense(id: String, retry: Boolean = true): Result<MessagePayload, MessageError>

    // Stats
    suspend fun getStatistics(retry: Boolean = true): Result<AllStats, MessageError>
}

class NetworkWanderWalletApiRepository(
    private val wanderWalletApiService: WanderWalletApiService,
    private val tokenProvider: TokenProvider
): WanderWalletApiRepository {

    private val gson = Gson()

    // User

    override suspend fun registerUser(email: String, username: String, password: String, avatarUri: Uri?, contentResolver: ContentResolver?): Result<UserAndTokensPayload, UserError> {
        val emailPart = email.toRequestBody("text/plain".toMediaType())
        val usernamePart = username.toRequestBody("text/plain".toMediaType())
        val passwordPart = password.toRequestBody("text/plain".toMediaType())
        var avatarPart: MultipartBody.Part? = null
        if (avatarUri != null && contentResolver != null) {
            val inputStream = contentResolver.openInputStream(avatarUri)
            val fileName = avatarUri.lastPathSegment ?: "avatar.jpg"
            val imageBytes = inputStream?.readBytes() ?: throw Error()
            avatarPart = MultipartBody.Part.createFormData(
                "avatar",
                fileName,
                imageBytes.toRequestBody("image/*".toMediaTypeOrNull())
            )
        }

        return try {
            val response =  wanderWalletApiService.registerUser(
                username = usernamePart,
                email = emailPart,
                password = passwordPart,
                avatar = avatarPart
            )

            tokenProvider.setAccessToken(response.accessToken)
            tokenProvider.setRefreshToken(response.refreshToken)
            Result.Success(response)
        } catch (e: HttpException) {
            try {
                val errorBody = e.response()?.errorBody()
                val errorResponse = gson.fromJson(errorBody?.string(), UserError::class.java)

                Result.Error(errorResponse)
            } catch (e: Throwable) {
                Result.Error(UserError(message = e.message))
            }
        } catch (e: IOException) {
            Result.Error(UserError(message = "Check your internet connection or the API address"))
        }
    }

    override suspend fun loginUser(email: String, password: String): Result<TokensPayload, UserError> {
        val requestBody = LoginRequest(email, password)

        return try {
            val response = wanderWalletApiService.loginUser(requestBody)
            tokenProvider.setAccessToken(response.accessToken)
            tokenProvider.setRefreshToken(response.refreshToken)

            Result.Success(response)
        } catch (e: HttpException) {
            try {
                val errorBody = e.response()?.errorBody()
                val errorResponse = gson.fromJson(errorBody?.string(), UserError::class.java)

                Result.Error(errorResponse)
            } catch (e: Throwable) {
                Result.Error(UserError(message = e.message ?: "An unexpected error occurred"))
            }
        } catch (e: IOException) {
            Result.Error(UserError(message = "Check your internet connection or the API address"))
        }
    }

    override suspend fun refreshToken(): Result<TokensPayload, MessageError> {
        val refreshToken = tokenProvider.getRefreshToken()
        val refreshTokenRequest = RefreshTokenRequest(refreshToken ?: "")

        return try {
            val response = wanderWalletApiService.refreshToken(refreshTokenRequest)

            tokenProvider.setAccessToken(response.accessToken)
            tokenProvider.setRefreshToken(response.refreshToken)
            Result.Success(response)
        } catch (e: HttpException) {
            try {
                val errorBody = e.response()?.errorBody()
                val errorResponse = gson.fromJson(errorBody?.string(), MessageError::class.java)

                Result.Error(errorResponse)
            } catch (e: Throwable) {
                Result.Error(MessageError("An unexpected error occurred"))
            }
        } catch (e: IOException) {
            Result.Error(MessageError(message = "Check your internet connection or the API address"))
        }
    }

    override suspend fun getProfile(retry: Boolean): Result<UserPayload, MessageError> {
        return try {
            val response = wanderWalletApiService.getProfile()

            Result.Success(response)
        } catch (e: HttpException) {
            if (e.code() == 401) {
                if (retry) {
                    try {
                        refreshToken()
                        getProfile(retry = false)
                    } catch (e: HttpException) {
                        Result.Error(MessageError("Please login and continue"), loggedOut = true)
                    } catch (e: IOException) {
                        Result.Error(MessageError("Check your internet connection or the API address"))
                    }
                } else {
                    Result.Error(MessageError("Please login and continue"), loggedOut = true)
                }
            } else {
                try {
                    val errorBody = e.response()?.errorBody()
                    val errorResponse = gson.fromJson(errorBody?.string(), MessageError::class.java)
                    Result.Error(errorResponse, loggedOut = true)
                } catch (e: Throwable) {
                    Result.Error(MessageError("An unexpected error occurred"))
                }
            }
        } catch (e: IOException) {
            Result.Error(MessageError("Check your internet connection or the API address"))
        }
    }

    override suspend fun updateProfile(username: String, avatarUri: Uri?, contentResolver: ContentResolver?, retry: Boolean): Result<UserPayload, UserError> {
        val usernamePart = username.toRequestBody("text/plain".toMediaType())
        var avatarPart: MultipartBody.Part? = null

        if (avatarUri != null && contentResolver != null) {
            val inputStream = contentResolver.openInputStream(avatarUri)
            val fileName = avatarUri.lastPathSegment ?: "avatar.jpg"
            val imageBytes = inputStream?.readBytes() ?: throw Error()
            avatarPart = MultipartBody.Part.createFormData(
                "avatar",
                fileName,
                imageBytes.toRequestBody("image/*".toMediaTypeOrNull())
            )
        }

        return try {
            val response = wanderWalletApiService.updateProfile(usernamePart, avatarPart)

            Result.Success(response)
        } catch (e: HttpException) {
            if (e.code() == 401) {
                if (retry) {
                    try {
                        refreshToken()
                        updateProfile(username, avatarUri, contentResolver, retry = false)
                    } catch (e: HttpException) {
                        Result.Error(UserError(message = "Please login and continue"), loggedOut = true)
                    }
                } else {
                    Result.Error(UserError(message = "Please login and continue"), loggedOut = true)
                }
            } else {
                try {
                    val errorBody = e.response()?.errorBody()
                    val errorResponse = gson.fromJson(errorBody?.string(), UserError::class.java)

                    Result.Error(errorResponse)
                } catch (e: Throwable) {
                    Result.Error(UserError(message = e.message))
                }
            }
        } catch (e: IOException) {
            Result.Error(UserError(message = "Check your internet connection or the API address"))
        }
    }

    override suspend fun getNotifications(retry: Boolean): Result<NotificationsPayload, MessageError> {
        return try {
            val response = wanderWalletApiService.getNotifications()

            Result.Success(response)
        } catch (e: HttpException) {
            if (e.code() == 401) {
                if (retry) {
                    try {
                        refreshToken()
                        getNotifications(retry = false)
                    } catch (e: HttpException) {
                        Result.Error(MessageError("Please login and continue"), loggedOut = true)
                    } catch (e: IOException) {
                        Result.Error(MessageError("Check your internet connection or the API address"))
                    }
                } else {
                    Result.Error(MessageError("Please login and continue"), loggedOut = true)
                }
            } else {
                try {
                    val errorBody = e.response()?.errorBody()
                    val errorResponse = gson.fromJson(errorBody?.string(), MessageError::class.java)
                    Result.Error(errorResponse, loggedOut = true)
                } catch (e: Throwable) {
                    Result.Error(MessageError("An unexpected error occurred"))
                }
            }
        } catch (e: IOException) {
            Result.Error(MessageError("Check your internet connection or the API address"))
        }
    }

    override suspend fun logoutUser(retry: Boolean): Result<MessagePayload, TokensError> {
        val refreshToken = tokenProvider.getRefreshToken()
        val refreshTokenRequest = RefreshTokenRequest(refreshToken ?: "")
        return try {
            val response = wanderWalletApiService.logoutUser(refreshTokenRequest)

            Result.Success(response)
        } catch (e: HttpException) {
            if (e.code() == 401) {
                if (retry) {
                    try {
                        refreshToken()
                        logoutUser(retry = false)
                    } catch (e: HttpException) {
                        Result.Error(
                            TokensError(message = "User is not logged in"),
                            loggedOut = true
                        )
                    }
                } else {
                    Result.Error(
                        TokensError(message = "User is not logged in"),
                        loggedOut = true
                    )
                }
            } else {
                try {
                    val errorBody = e.response()?.errorBody()
                    val errorResponse = gson.fromJson(errorBody?.string(), TokensError::class.java)

                    Result.Error(errorResponse)
                } catch (e: Throwable) {
                    Result.Error(TokensError(message = "An unexpected error occurred"))
                }
            }
        } catch (e: IOException) {
            Result.Error(TokensError(message = "Check your internet connection or the API address"))
        }
    }

    // Trip
    override suspend fun createTrip(
        name: String,
        destination: String,
        budget: String,
        startDate: String,
        endDate: String,
        tripImageUri: Uri?,
        contentResolver: ContentResolver?,
        retry: Boolean
    ): Result<TripPayload, TripError> {
        val namePart = name.toRequestBody("text/plain".toMediaType())
        val destinationPart = destination.toRequestBody("text/plain".toMediaType())
        val budgetPart = budget.toRequestBody("text/plain".toMediaType())
        val startDatePart = startDate.toRequestBody("text/plain".toMediaType())
        val endDatePart = endDate.toRequestBody("text/plain".toMediaType())
        var tripImagePart: MultipartBody.Part? = null

        if (tripImageUri != null && contentResolver != null) {
            val inputStream = contentResolver.openInputStream(tripImageUri)
            val fileName = tripImageUri.lastPathSegment ?: "avatar.jpg"
            val imageBytes = inputStream?.readBytes() ?: throw Error()
            tripImagePart = MultipartBody.Part.createFormData(
                "avatar",
                fileName,
                imageBytes.toRequestBody("image/*".toMediaTypeOrNull())
            )
        }

        return try {
            val response = wanderWalletApiService.createTrip(
                name = namePart,
                destination = destinationPart,
                budget = budgetPart,
                startDate = startDatePart,
                endDate = endDatePart,
                tripImage = tripImagePart
            )

            Result.Success(response)
        } catch (e: HttpException) {
            if (e.code() == 401) {
                if (retry) {
                    try {
                        refreshToken()
                        createTrip(
                            name,
                            destination,
                            budget,
                            startDate,
                            endDate,
                            tripImageUri,
                            contentResolver,
                            retry = false
                        )
                    } catch (e: HttpException) {
                        Result.Error(
                            TripError(message = "Please login and continue"),
                            loggedOut = true
                        )
                    }
                } else {
                    Result.Error(
                        TripError(message = "Please login and continue"),
                        loggedOut = true
                    )
                }
            } else {
                try {
                    val errorBody = e.response()?.errorBody()
                    val errorResponse = gson.fromJson(errorBody?.string(), TripError::class.java)

                    Result.Error(errorResponse)
                } catch (e: Throwable) {
                    Result.Error(TripError(message = "An unexpected error occurred"))
                }
            }
        } catch (e: IOException) {
            Result.Error(TripError(message = "Check your internet connection or the API address"))
        }
    }

    override suspend fun getTrips(retry: Boolean): Result<TripsPayload, MessageError> {
        return try {
            val response = wanderWalletApiService.getTrips()

            Result.Success(response)
        } catch (e: HttpException) {
            if (e.code() == 401) {
                if (retry) {
                    try {
                        refreshToken()
                        getTrips(retry = false)
                    } catch (e: HttpException) {
                        Result.Error(MessageError("Please login and continue"), loggedOut = true)
                    }
                } else {
                    Result.Error(MessageError("Please login and continue"), loggedOut = true)
                }
            } else {
                try {
                    val errorBody = e.response()?.errorBody()
                    val errorResponse = gson.fromJson(errorBody?.string(), MessageError::class.java)

                    Result.Error(errorResponse)
                } catch (e: Throwable) {
                    Result.Error(MessageError(message = "An unexpected error occurred"))
                }
            }
        } catch (e: IOException) {
            Result.Error(MessageError(message = "Check your internet connection or the API address"))
        }
    }

    override suspend fun getPendingTrips(retry: Boolean): Result<TripsPayload, MessageError> {
        return try {
            val response = wanderWalletApiService.getPendingTrips()

            Result.Success(response)
        } catch (e: HttpException) {
            if (e.code() == 401) {
                if (retry) {
                    try {
                        refreshToken()
                        getPendingTrips(retry = false)
                    } catch (e: HttpException) {
                        Result.Error(MessageError("Please login and continue"), loggedOut = true)
                    }
                } else {
                    Result.Error(MessageError("Please login and continue"), loggedOut = true)
                }
            } else {
                try {
                    val errorBody = e.response()?.errorBody()
                    val errorResponse = gson.fromJson(errorBody?.string(), MessageError::class.java)

                    Result.Error(errorResponse)
                } catch (e: Throwable) {
                    Result.Error(MessageError(message = "An unexpected error occurred"))
                }
            }
        } catch (e: IOException) {
            Result.Error(MessageError(message = "Check your internet connection or the API address"))
        }
    }

    override suspend fun getCurrentTrips(retry: Boolean): Result<TripsPayload, MessageError> {
        return try {
            val response = wanderWalletApiService.getCurrentTrips()

            Result.Success(response)
        } catch (e: HttpException) {
            if (e.code() == 401) {
                if (retry) {
                    try {
                        refreshToken()
                        getCurrentTrips(retry = false)
                    } catch (e: HttpException) {
                        Result.Error(MessageError("Please login and continue"), loggedOut = true)
                    }
                } else {
                    Result.Error(MessageError("Please login and continue"), loggedOut = true)
                }
            } else {
                try {
                    val errorBody = e.response()?.errorBody()
                    val errorResponse = gson.fromJson(errorBody?.string(), MessageError::class.java)

                    Result.Error(errorResponse)
                } catch (e: Throwable) {
                    Result.Error(MessageError(message = "An unexpected error occurred"))
                }
            }
        } catch (e: IOException) {
            Result.Error(MessageError(message = "Check your internet connection or the API address"))
        }
    }

    override suspend fun getPastTrips(retry: Boolean): Result<TripsPayload, MessageError> {
        return try {
            val response = wanderWalletApiService.getPastTrips()

            Result.Success(response)
        } catch (e: HttpException) {
            if (e.code() == 401) {
                if (retry) {
                    try {
                        refreshToken()
                        getPastTrips(retry = false)
                    } catch (e: HttpException) {
                        Result.Error(MessageError("Please login and continue"), loggedOut = true)
                    }
                } else {
                    Result.Error(MessageError("Please login and continue"), loggedOut = true)
                }
            } else {
                try {
                    val errorBody = e.response()?.errorBody()
                    val errorResponse = gson.fromJson(errorBody?.string(), MessageError::class.java)

                    Result.Error(errorResponse)
                } catch (e: Throwable) {
                    Result.Error(MessageError(message = "An unexpected error occurred"))
                }
            }
        } catch (e: IOException) {
            Result.Error(MessageError(message = "Check your internet connection or the API address"))
        }    }

    override suspend fun getTrip(id: String, retry: Boolean): Result<TripPayload, MessageError> {
        return try {
            val response = wanderWalletApiService.getTrip(id)

            Result.Success(response)
        } catch (e: HttpException) {
            if (e.code() == 401) {
                if (retry) {
                    try {
                        refreshToken()
                        getTrip(id, retry = false)
                    } catch (e: HttpException) {
                        Result.Error(MessageError(message = "Please login and continue"), loggedOut = true)
                    }
                } else {
                    Result.Error(MessageError(message = "Please login and continue"), loggedOut = true)
                }
            } else {
                try {
                    val errorBody = e.response()?.errorBody()
                    val errorResponse = gson.fromJson(errorBody?.string(), MessageError::class.java)

                    Result.Error(errorResponse)
                } catch (e: Throwable) {
                    Result.Error(MessageError(message = "An unexpected error occurred"))
                }
            }
        } catch (e: IOException) {
            Result.Error(MessageError(message = "Check your internet connection or the API address"))
        }
    }

    override suspend fun deleteTrip(id: String, retry: Boolean): Result<MessagePayload, MessageError> {
        return try {
            val response = wanderWalletApiService.deleteTrip(id)

            Result.Success(response)
        } catch (e: HttpException) {
            if (e.code() == 401) {
                if (retry) {
                    try {
                        refreshToken()
                        deleteTrip(id, retry = false)
                    } catch (e: HttpException) {
                        Result.Error(MessageError(message = "Please login and continue"), loggedOut = true)
                    }
                } else {
                    Result.Error(MessageError(message = "Please login and continue"), loggedOut = true)
                }
            } else {
                try {
                    val errorBody = e.response()?.errorBody()
                    val errorResponse = gson.fromJson(errorBody?.string(), MessageError::class.java)

                    Result.Error(errorResponse)
                } catch (e: Throwable) {
                    Result.Error(MessageError(message = "An unexpected error occurred"))
                }
            }
        } catch (e: IOException) {
            Result.Error(MessageError(message = "Check your internet connection or the API address"))
        }
    }

    override suspend fun updateTrip(
        id: String,
        name: String,
        destination: String,
        budget: String,
        startDate: String,
        endDate: String,
        tripImageUri: Uri?,
        contentResolver: ContentResolver?,
        retry: Boolean
    ): Result<TripPayload, TripError> {
        val namePart = name.toRequestBody("text/plain".toMediaType())
        val destinationPart = destination.toRequestBody("text/plain".toMediaType())
        val budgetPart = budget.toRequestBody("text/plain".toMediaType())
        val startDatePart = startDate.toRequestBody("text/plain".toMediaType())
        val endDatePart = endDate.toRequestBody("text/plain".toMediaType())
        var tripImagePart: MultipartBody.Part? = null

        if (tripImageUri != null && contentResolver != null) {
            val inputStream = contentResolver.openInputStream(tripImageUri)
            val fileName = tripImageUri.lastPathSegment ?: "avatar.jpg"
            val imageBytes = inputStream?.readBytes() ?: throw Error()
            tripImagePart = MultipartBody.Part.createFormData(
                "avatar",
                fileName,
                imageBytes.toRequestBody("image/*".toMediaTypeOrNull())
            )
        }

        return try {
            val response = wanderWalletApiService.updateTrip(
                id = id,
                name = namePart,
                destination = destinationPart,
                budget = budgetPart,
                startDate = startDatePart,
                endDate = endDatePart,
                tripImage = tripImagePart
            )

            Result.Success(response)
        } catch (e: HttpException) {
            if (e.code() == 401) {
                if (retry) {
                    try {
                        refreshToken()
                        updateTrip(
                            id,
                            name,
                            destination,
                            budget,
                            startDate,
                            endDate,
                            tripImageUri,
                            contentResolver,
                            retry = false
                        )
                    } catch (e: HttpException) {
                        Result.Error(
                            TripError(message = "Please login and continue"),
                            loggedOut = true
                        )
                    }
                } else {
                    Result.Error(
                        TripError(message = "Please login and continue"),
                        loggedOut = true
                    )
                }
            } else {
                try {
                    val errorBody = e.response()?.errorBody()
                    val errorResponse = gson.fromJson(errorBody?.string(), TripError::class.java)

                    Result.Error(errorResponse)
                } catch (e: Throwable) {
                    Result.Error(TripError(message = "An unexpected error occurred"))
                }
            }
        } catch (e: IOException) {
            Result.Error(TripError(message = "Check your internet connection or the API address"))
        }
    }

    // Expense
    override suspend fun createExpense(
        tripId: String,
        name: String,
        amount: String,
        category: Category,
        date: String,
        notes: String?,
        retry: Boolean
    ): Result<ExpensePayload, ExpenseError> {
        val requestBody = CreateExpenseRequest(
            name = name,
            amount = amount,
            category = category,
            date = date,
            notes = notes
        )

        return try {
            val response = wanderWalletApiService.createExpense(tripId, requestBody)

            Result.Success(response)
        } catch (e: HttpException) {
            if (e.code() == 401) {
                if (retry) {
                    try {
                        refreshToken()
                        createExpense(tripId, name, amount, category, date, notes, retry = false)
                    } catch (e: HttpException) {
                        Result.Error(ExpenseError(message = "Please login and continue"), loggedOut = true)
                    }
                } else {
                    Result.Error(ExpenseError(message = "Please login and continue"), loggedOut = true)
                }
            } else {
                try {
                    val errorBody = e.response()?.errorBody()
                    val errorResponse = gson.fromJson(errorBody?.string(), ExpenseError::class.java)

                    Result.Error(errorResponse)
                } catch (e: Throwable) {
                    Result.Error(ExpenseError(message = "An unexpected error occurred"))
                }
            }
        } catch (e: IOException) {
            Result.Error(ExpenseError(message = "Check your internet connection or the API address"))
        }
    }

    override suspend fun getTripExpenses(tripId: String, retry: Boolean): Result<ExpensesPayload, MessageError> {
        return try {
            val response = wanderWalletApiService.getTripExpenses(tripId)

            Result.Success(response)
        } catch (e: HttpException) {
            if (e.code() == 401) {
                if (retry) {
                    try {
                        refreshToken()
                        getTripExpenses(tripId, retry = false)
                    } catch (e: HttpException) {
                        Result.Error(MessageError(message = "Please login and continue"), loggedOut = true)
                    }
                } else {
                    Result.Error(MessageError(message = "Please login and continue"), loggedOut = true)
                }
            } else {
                try {
                    val errorBody = e.response()?.errorBody()
                    val errorResponse = gson.fromJson(errorBody?.string(), MessageError::class.java)

                    Result.Error(errorResponse)
                } catch (e: Throwable) {
                    Result.Error(MessageError(message = "An unexpected error occurred"))
                }
            }
        } catch (e: IOException) {
            Result.Error(MessageError(message = "Check your internet connection or the API address"))
        }
    }

    override suspend fun getExpense(id: String, retry: Boolean): Result<ExpensePayload, MessageError> {
        return try {
            val response = wanderWalletApiService.getExpense(id)

            Result.Success(response)
        } catch (e: HttpException) {
            if (e.code() == 401) {
                if (retry) {
                    try {
                        refreshToken()
                        getExpense(id, retry = false)
                    } catch (e: HttpException) {
                        Result.Error(MessageError(message = "Please login and continue"), loggedOut = true)
                    }
                } else {
                    Result.Error(MessageError(message = "Please login and continue"), loggedOut = true)
                }
            } else {
                try {
                    val errorBody = e.response()?.errorBody()
                    val errorResponse = gson.fromJson(errorBody?.string(), MessageError::class.java)

                    Result.Error(errorResponse)
                } catch (e: Throwable) {
                    Result.Error(MessageError(message = "An unexpected error occurred"))
                }
            }
        } catch (e: IOException) {
            Result.Error(MessageError(message = "Check your internet connection or the API address"))
        }
    }

    override suspend fun updateExpense(
        id: String,
        name: String,
        amount: String,
        category: Category,
        date: String,
        notes: String?,
        retry: Boolean
    ): Result<ExpensePayload, ExpenseError> {
        val requestBody = CreateExpenseRequest(
            name = name,
            amount = amount,
            category = category,
            date = date,
            notes = notes
        )

        return try {
            val response = wanderWalletApiService.updateExpense(id, requestBody)

            Result.Success(response)
        } catch (e: HttpException) {
            if (e.code() == 401) {
                if (retry) {
                    try {
                        refreshToken()
                        updateExpense(id, name, amount, category, date, notes, retry = false)
                    } catch (e: HttpException) {
                        Result.Error(ExpenseError(message = "Please login and continue"), loggedOut = true)
                    }
                } else {
                    Result.Error(ExpenseError(message = "Please login and continue"), loggedOut = true)
                }
            } else {
                try {
                    val errorBody = e.response()?.errorBody()
                    val errorResponse = gson.fromJson(errorBody?.string(), ExpenseError::class.java)

                    Result.Error(errorResponse)
                } catch (e: Throwable) {
                    Result.Error(ExpenseError(message = "An unexpected error occurred"))
                }
            }
        } catch (e: IOException) {
            Result.Error(ExpenseError(message = "Check your internet connection or the API address"))
        }
    }

    override suspend fun deleteExpense(id: String, retry: Boolean): Result<MessagePayload, MessageError> {
        return try {
            val response = wanderWalletApiService.deleteExpense(id)

            Result.Success(response)
        } catch (e: HttpException) {
            if (e.code() == 401) {
                if (retry) {
                    try {
                        refreshToken()
                        deleteExpense(id, retry = false)
                    } catch (e: HttpException) {
                        Result.Error(MessageError(message = "Please login and continue"), loggedOut = true)
                    }
                } else {
                    Result.Error(MessageError(message = "Please login and continue"), loggedOut = true)
                }
            } else {
                try {
                    val errorBody = e.response()?.errorBody()
                    val errorResponse = gson.fromJson(errorBody?.string(), MessageError::class.java)

                    Result.Error(errorResponse)
                } catch (e: Throwable) {
                    Result.Error(MessageError(message = "An unexpected error occurred"))
                }
            }
        } catch (e: IOException) {
            Result.Error(MessageError(message = "Check your internet connection or the API address"))
        }
    }

    // Stats
    override suspend fun getStatistics(retry: Boolean): Result<AllStats, MessageError> {
        return try {
            val totalSpending = wanderWalletApiService.getTotalSpending()
            val avgSpendingPerTrip = wanderWalletApiService.getAvgSpendingPerTrip()
            val avgSpendingPerDay = wanderWalletApiService.getAvgSpendingPerDay()
            val spendingByCategory = wanderWalletApiService.getSpendingByCategory()
            val spendingByMonth = wanderWalletApiService.getMonthlySpending()
            val budgetComparison = wanderWalletApiService.getBudgetComparison()
            val mostExpensiveTrip = wanderWalletApiService.getMostExpensiveTrip()
            val leastExpensiveTrip = wanderWalletApiService.getLeastExpensiveTrip()

            Result.Success(AllStats(
                totalSpending = totalSpending.totalSpending,
                totalBudget = totalSpending.totalBudget,
                avgSpendingPerTrip = avgSpendingPerTrip.avgSpending,
                avgSpendingPerDay = avgSpendingPerDay.avgSpending,
                spendingByCategory = spendingByCategory.categories,
                monthlySpending = spendingByMonth.expensesByMonth,
                budgetComparisons = budgetComparison.budgetComparison,
                mostExpensiveTrip = mostExpensiveTrip,
                leastExpensiveTrip = leastExpensiveTrip
            ))
        } catch (e: HttpException) {
            if (e.code() == 401) {
                if (retry) {
                    try {
                        refreshToken()
                        getStatistics()
                    } catch (e: HttpException) {
                        Result.Error(MessageError(message = "Please login and continue"), loggedOut = true)
                    }
                } else {
                    Result.Error(MessageError(message = "Please login and continue"), loggedOut = true)
                }
            } else {
                try {
                    val errorBody = e.response()?.errorBody()
                    val errorResponse = gson.fromJson(errorBody?.string(), MessageError::class.java)

                    Result.Error(errorResponse)
                } catch (e: Throwable) {
                    Result.Error(MessageError(message = "An unexpected error occurred"))
                }
            }
        }
    }
}
