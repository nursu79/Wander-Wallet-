package com.mobile.wanderwallet.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.wanderwallet.data.model.Result
import com.mobile.wanderwallet.data.model.TokensPayload
import com.mobile.wanderwallet.data.model.UserError
import com.mobile.wanderwallet.data.repository.WanderWalletApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

sealed interface LoginScreenUiState {
    data object Waiting: LoginScreenUiState
    data object Loading: LoginScreenUiState
    data class Success(val data: TokensPayload): LoginScreenUiState
    data class Error(val error: UserError, val loggedOut: Boolean = false): LoginScreenUiState
}

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val apiRepository: WanderWalletApiRepository
): ViewModel() {
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var loginScreenUiState: LoginScreenUiState by mutableStateOf(LoginScreenUiState.Waiting)

    fun updateEmail(value: String) {
        email = value
    }

    fun updatePassword(value: String) {
        password = value
    }

    fun loginUser() {
        viewModelScope.launch {
            loginScreenUiState = LoginScreenUiState.Loading
            try {
                val response = apiRepository.loginUser(email, password)
                loginScreenUiState = when (response) {
                    is Result.Error<UserError> -> LoginScreenUiState.Error(response.error, loggedOut = response.loggedOut)
                    is Result.Success<TokensPayload> -> LoginScreenUiState.Success(response.data)
                }
            } catch (e: HttpException) {
                loginScreenUiState = LoginScreenUiState.Error(UserError(message = "An unexpected error occurred"))
            }
        }
    }
}