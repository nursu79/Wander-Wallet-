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

sealed interface MainContentScreenUiState {
    data class Success(val data: UserPayload): MainContentScreenUiState
    data class Error(val error: MessageError, val loggedOut: Boolean = false): MainContentScreenUiState
    data object Loading: MainContentScreenUiState
}

@HiltViewModel
class MainContentScreenViewModel @Inject constructor(
    private val apiRepository: WanderWalletApiRepository
): ViewModel() {
    var mainContentScreenUiState: MainContentScreenUiState by mutableStateOf(MainContentScreenUiState.Loading)
        private set

    init {
        getUser()
    }

    fun getUser() {
        viewModelScope.launch {
            mainContentScreenUiState = MainContentScreenUiState.Loading
            try {
                val response = apiRepository.getProfile()
                mainContentScreenUiState = when (response) {
                    is Result.Error<MessageError> -> MainContentScreenUiState.Error(response.error, loggedOut = response.loggedOut)
                    is Result.Success<UserPayload> -> MainContentScreenUiState.Success(response.data)
                }
            } catch (e: HttpException) {
                MainContentScreenUiState.Error(error = MessageError("An unexpected error occurred"))
            }
        }
    }
}