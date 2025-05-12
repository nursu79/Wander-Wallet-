package com.mobile.wanderwallet.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.wanderwallet.data.model.AllStats
import com.mobile.wanderwallet.data.model.MessageError
import com.mobile.wanderwallet.data.model.Result
import com.mobile.wanderwallet.data.repository.WanderWalletApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

sealed interface SummaryScreenUiState {
    data class Success(val data: AllStats): SummaryScreenUiState
    data class Error(val error: MessageError, val loggedOut: Boolean = false): SummaryScreenUiState
    data object Loading: SummaryScreenUiState
}

@HiltViewModel
class SummaryScreenViewModel @Inject constructor(
    private val apiRepository: WanderWalletApiRepository
): ViewModel() {
    var summaryScreenUiState: SummaryScreenUiState by mutableStateOf(SummaryScreenUiState.Loading)

    init {
        getStats()
    }

    fun getStats() {
        viewModelScope.launch {
            summaryScreenUiState = SummaryScreenUiState.Loading
            try {
                val response = apiRepository.getStatistics()

                summaryScreenUiState = when (response) {
                    is Result.Error<MessageError> -> SummaryScreenUiState.Error(response.error)
                    is Result.Success<AllStats> -> SummaryScreenUiState.Success(response.data)
                }
            } catch (e: HttpException) {
                summaryScreenUiState = SummaryScreenUiState.Error(MessageError("An unexpected error occurred"))
            }
        }
    }
}