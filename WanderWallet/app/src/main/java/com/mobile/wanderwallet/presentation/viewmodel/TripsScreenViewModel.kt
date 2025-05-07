package com.mobile.wanderwallet.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.wanderwallet.data.model.MessageError
import com.mobile.wanderwallet.data.model.Result
import com.mobile.wanderwallet.data.model.TripsPayload
import com.mobile.wanderwallet.data.repository.WanderWalletApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

sealed interface TripsScreenUiState {
    data class Success(val data: TripsPayload): TripsScreenUiState
    data class Error(val error: MessageError, val loggedOut: Boolean = false): TripsScreenUiState
    data object Loading: TripsScreenUiState
}

enum class Timeframe(val index: Int) {
    Past(index = 0),
    Current(index = 1),
    Pending(index = 2)
}

@HiltViewModel
class TripsScreenViewModel @Inject constructor(
    private val apiRepository: WanderWalletApiRepository
): ViewModel() {
    var tripsScreenUiState: TripsScreenUiState by mutableStateOf(TripsScreenUiState.Loading)
        private set
    var timeframe: Timeframe by mutableStateOf(Timeframe.Current)
        private set

    init {
        getTrips()
    }

    fun updateTimeframe(value: Timeframe) {
        if (timeframe != value) {
            timeframe = value
            getTrips()
        }
    }

    fun getTrips() {
        viewModelScope.launch {
            tripsScreenUiState = TripsScreenUiState.Loading
            try {
                val response = when (timeframe) {
                    Timeframe.Past -> apiRepository.getPastTrips()
                    Timeframe.Current -> apiRepository.getCurrentTrips()
                    Timeframe.Pending -> apiRepository.getPendingTrips()
                }

                tripsScreenUiState = when (response) {
                    is Result.Error<MessageError> -> TripsScreenUiState.Error(response.error, loggedOut = response.loggedOut)
                    is Result.Success<TripsPayload> -> TripsScreenUiState.Success(response.data)
                }
            } catch (e: HttpException) {
                tripsScreenUiState = TripsScreenUiState.Error(MessageError("An unexpected error occurred"))
            }
        }
    }
}