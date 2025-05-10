package com.mobile.wanderwallet.presentation.viewmodel

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.wanderwallet.data.model.MessageError
import com.mobile.wanderwallet.data.model.Result
import com.mobile.wanderwallet.data.model.TripError
import com.mobile.wanderwallet.data.model.TripPayload
import com.mobile.wanderwallet.data.repository.WanderWalletApiRepository
import com.mobile.wanderwallet.utils.convertDateToFormattedString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

sealed interface EditTripScreenUiState {
    data class Success(val data: TripPayload): EditTripScreenUiState
    data class Error(val error: TripError, val loggedOut: Boolean = false): EditTripScreenUiState
    data object Loading: EditTripScreenUiState
    data object Waiting: EditTripScreenUiState
}

@HiltViewModel
class EditTripScreenViewModel @Inject constructor(
    savesStateHandle: SavedStateHandle,
    private val apiRepository: WanderWalletApiRepository
): ViewModel() {
    private val tripId = savesStateHandle.get<String>("tripId") ?: ""
    var editTripScreenUiState: EditTripScreenUiState by mutableStateOf(EditTripScreenUiState.Waiting)

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
    var tripImageUrl: String? by mutableStateOf(null)
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

    init {
        getTrip()
    }

    private fun getTrip() {
        viewModelScope.launch {
            try {
                when (val response = apiRepository.getTrip(tripId)) {
                    is Result.Error<MessageError> -> {
                        editTripScreenUiState = EditTripScreenUiState.Error(
                            TripError(message = response.error.message),
                            loggedOut = response.loggedOut
                        )
                    }
                    is Result.Success<TripPayload> -> {
                        val trip = response.data.trip

                        name = trip?.name ?: ""
                        destination = trip?.destination ?: ""
                        budget = trip?.budget.toString()
                        startDate = convertDateToFormattedString(trip?.startDate) ?: ""
                        endDate = convertDateToFormattedString(trip?.endDate) ?: ""
                        tripImageUrl = trip?.imgUrl
                    }
                }
            } catch (e: HttpException) {
                editTripScreenUiState = EditTripScreenUiState.Error(
                    TripError(message = "An unexpected error occurred")
                )
            }
        }
    }

    fun updateTrip(imageUri: Uri? = null, contentResolver: ContentResolver? = null) {
        viewModelScope.launch {
            editTripScreenUiState = EditTripScreenUiState.Loading
            try {
                val response = apiRepository.updateTrip(
                    id = tripId,
                    name = name,
                    destination = destination,
                    budget = budget,
                    startDate = startDate,
                    endDate = endDate,
                    tripImageUri = imageUri,
                    contentResolver = contentResolver
                )

                editTripScreenUiState = when (response) {
                    is Result.Error<TripError> -> EditTripScreenUiState.Error(response.error, loggedOut = response.loggedOut)
                    is Result.Success<TripPayload> -> EditTripScreenUiState.Success(response.data)
                }
            } catch (e: HttpException) {
                editTripScreenUiState = EditTripScreenUiState.Error(TripError(message = "An unexpected error occurred"))
            }
        }
    }
}