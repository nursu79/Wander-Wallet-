package com.mobile.wanderwallet.presentation.viewmodel

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.wanderwallet.data.model.Result
import com.mobile.wanderwallet.data.model.UserAndTokensPayload
import com.mobile.wanderwallet.data.model.UserError
import com.mobile.wanderwallet.data.repository.WanderWalletApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

sealed interface RegisterScreenUiState {
    data object Waiting: RegisterScreenUiState
    data object Loading: RegisterScreenUiState
    data class Success(val data: UserAndTokensPayload): RegisterScreenUiState
    data class Error(val error: UserError, val loggedOut: Boolean = false): RegisterScreenUiState
}

@HiltViewModel
class RegisterScreenViewModel @Inject constructor(
    private val apiRepository: WanderWalletApiRepository
): ViewModel() {
    var username by mutableStateOf("")
        private set
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set

    var registerScreenUiState: RegisterScreenUiState by mutableStateOf(RegisterScreenUiState.Waiting)

    fun updateUsername(value: String) {
        username = value
    }

    fun updateEmail(value: String) {
        email = value
    }

    fun updatePassword(value: String) {
        password = value
    }

    fun registerUser(imageUri: Uri? = null, contentResolver: ContentResolver? = null) {
        viewModelScope.launch {
            registerScreenUiState = RegisterScreenUiState.Loading
            try {
                val response = apiRepository.registerUser(email, username, password, avatarUri = imageUri, contentResolver = contentResolver)
                registerScreenUiState = when (response) {
                    is Result.Error<UserError> -> RegisterScreenUiState.Error(response.error, loggedOut = response.loggedOut)
                    is Result.Success<UserAndTokensPayload> -> RegisterScreenUiState.Success(response.data)
                }
            } catch (e: HttpException) {
                registerScreenUiState = RegisterScreenUiState.Error(UserError(message = "An unexpected error occurred"))
            }
        }
    }
}