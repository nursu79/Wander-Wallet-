package com.mobile.wanderwallet.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.wanderwallet.data.model.MessageError
import com.mobile.wanderwallet.data.model.Result
import com.mobile.wanderwallet.data.model.UserPayload
import com.mobile.wanderwallet.data.repository.WanderWalletApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

sealed interface SplashScreenUiState {
    data class Success(val data: UserPayload): SplashScreenUiState
    data class Error(val error: MessageError, val loggedOut: Boolean = false): SplashScreenUiState
    data object Loading: SplashScreenUiState
}

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val apiRepository: WanderWalletApiRepository
): ViewModel() {
    var splashScreenUiState: SplashScreenUiState by mutableStateOf(SplashScreenUiState.Loading)
        private set

    init {
        getUser()
    }

    fun getUser() {
        viewModelScope.launch {
            splashScreenUiState = SplashScreenUiState.Loading
            try {
                val response = apiRepository.getProfile()
                splashScreenUiState = when (response) {
                    is Result.Error<MessageError> -> SplashScreenUiState.Error(response.error, loggedOut = response.loggedOut)
                    is Result.Success<UserPayload> -> SplashScreenUiState.Success(response.data)
                }
            } catch (e: HttpException) {
                SplashScreenUiState.Error(error = MessageError("An unexpected error occurred"))
            }
        }
    }
}
