package com.mobile.wanderwallet.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.wanderwallet.data.model.MessageError
import com.mobile.wanderwallet.data.model.MessagePayload
import com.mobile.wanderwallet.data.model.Result
import com.mobile.wanderwallet.data.model.TripPayload
import com.mobile.wanderwallet.data.repository.WanderWalletApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

sealed interface TripDetailsScreenUiState {
    data class Success(val data: TripPayload): TripDetailsScreenUiState
    data class Error(val error: MessageError, val loggedOut: Boolean = false): TripDetailsScreenUiState
    data object Loading: TripDetailsScreenUiState
}

sealed interface TripDeleteState {
    data class Success(val data: MessagePayload): TripDeleteState
    data class Error(val error: MessageError, val loggedOut: Boolean = false): TripDeleteState
    data object Loading: TripDeleteState
    data object Waiting: TripDeleteState
}

@HiltViewModel
class TripDetailsScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val apiRepository: WanderWalletApiRepository,

    ): ViewModel() {
    var tripDetailsScreenUiState: TripDetailsScreenUiState by mutableStateOf(TripDetailsScreenUiState.Loading)
    var tripDeleteState: TripDeleteState by mutableStateOf(TripDeleteState.Waiting)
    private val tripId = savedStateHandle.get<String>("tripId") ?: ""

    init {
        getTripDetails()
    }

    fun getTripDetails() {
        viewModelScope.launch {
            tripDetailsScreenUiState = TripDetailsScreenUiState.Loading
            try {
                val response = apiRepository.getTrip(tripId)

                tripDetailsScreenUiState = when (response) {
                    is Result.Error<MessageError> -> TripDetailsScreenUiState.Error(response.error, loggedOut = response.loggedOut)
                    is Result.Success<TripPayload> -> TripDetailsScreenUiState.Success(response.data)
                }
            } catch (e: HttpException) {
                tripDetailsScreenUiState = TripDetailsScreenUiState.Error(MessageError("An unexpected error occurred"))
            }
        }
    }

    fun deleteTrip() {
        viewModelScope.launch {
            tripDeleteState = TripDeleteState.Loading
            try {
                val response = apiRepository.deleteTrip(tripId)

                tripDeleteState = when (response) {
                    is Result.Error<MessageError> -> TripDeleteState.Error(response.error)
                    is Result.Success<MessagePayload> -> TripDeleteState.Success(response.data)
                }
            } catch (e: HttpException) {
                tripDeleteState = TripDeleteState.Error(MessageError("An unexpected error occurred"))
            }
        }
    }
}