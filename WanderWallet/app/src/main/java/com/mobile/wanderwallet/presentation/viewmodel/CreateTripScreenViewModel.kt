package com.mobile.wanderwallet.presentation.viewmodel

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.wanderwallet.data.model.Result
import com.mobile.wanderwallet.data.model.TripError
import com.mobile.wanderwallet.data.model.TripPayload
import com.mobile.wanderwallet.data.repository.WanderWalletApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

sealed interface CreateTripScreenUiState {
    data object Waiting: CreateTripScreenUiState
    data object Loading: CreateTripScreenUiState
    data class Success(val data: TripPayload): CreateTripScreenUiState
    data class Error(val error: TripError, val loggedOut: Boolean = false): CreateTripScreenUiState
}

@HiltViewModel
class CreateTripScreenViewModel @Inject constructor(
    private val apiRepository: WanderWalletApiRepository
) : ViewModel() {
    var name by mutableStateOf("")
        private set
    var destination by mutableStateOf("")
        private set
    var budget by mutableStateOf("")
        private set
    var startDate by mutableStateOf("")
        private set
    var endDate by mutableStateOf("")
        private set

    fun updateName(value: String) {
        name = value
    }

    fun updateDestination(value: String) {
        destination = value
    }

    fun updateBudget(value: String) {
        budget = value
    }

    fun updateStartDate(value: String) {
        startDate = value
    }

    fun updateEndDate(value: String) {
        endDate = value
    }

    var createTripScreenUiState: CreateTripScreenUiState by mutableStateOf(CreateTripScreenUiState.Waiting)


    fun createTrip(imageUri: Uri? = null, contentResolver: ContentResolver? = null) {
        viewModelScope.launch {
            createTripScreenUiState = CreateTripScreenUiState.Loading
            try {
                val response = apiRepository.createTrip(
                    name = name,
                    destination = destination,
                    budget = budget,
                    startDate = startDate,
                    endDate = endDate,
                    tripImageUri = imageUri,
                    contentResolver = contentResolver
                )

                createTripScreenUiState = when (response) {
                    is Result.Error<TripError> -> CreateTripScreenUiState.Error(response.error, loggedOut = response.loggedOut)
                    is Result.Success<TripPayload> -> CreateTripScreenUiState.Success(response.data)
                }
            } catch (e: HttpException) {
                createTripScreenUiState = CreateTripScreenUiState.Error(TripError(message = "An unexpected error occurred"))
            }
        }
    }
}