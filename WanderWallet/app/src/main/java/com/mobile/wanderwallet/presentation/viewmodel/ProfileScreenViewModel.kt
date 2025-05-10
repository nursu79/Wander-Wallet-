package com.mobile.wanderwallet.presentation.viewmodel

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.wanderwallet.data.model.MessageError
import com.mobile.wanderwallet.data.model.MessagePayload
import com.mobile.wanderwallet.data.model.Result
import com.mobile.wanderwallet.data.model.TokensError
import com.mobile.wanderwallet.data.model.UserError
import com.mobile.wanderwallet.data.model.UserPayload
import com.mobile.wanderwallet.data.repository.WanderWalletApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

sealed interface ProfileScreenUiState {
    data class Success(val data: UserPayload): ProfileScreenUiState
    data class Error(val error: UserError, val loggedOut: Boolean = false): ProfileScreenUiState
    data object GetLoading: ProfileScreenUiState
    data object UpdateLoading: ProfileScreenUiState
    data object LogoutLoading: ProfileScreenUiState
    data object Waiting: ProfileScreenUiState
}

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val apiRepository: WanderWalletApiRepository
): ViewModel() {
    var profileScreenUiState: ProfileScreenUiState by mutableStateOf(ProfileScreenUiState.GetLoading)

    var username by mutableStateOf("")
        private set
    var email by mutableStateOf("")
        private set
    var newPassword by mutableStateOf("")
        private set
    var oldPassword by mutableStateOf("")
        private set
    var avatarUrl: String? by mutableStateOf(null)
        private set

    fun updateUsername(value: String) {
        username = value
    }

    fun updateEmail(value: String) {
        email = value
    }

    fun updateNewPassword(value: String) {
        newPassword = value
    }

    fun updateOldPassword(value: String) {
        oldPassword = value
    }

    init {
        getProfile()
    }

    private fun getProfile() {
        viewModelScope.launch {
            profileScreenUiState = ProfileScreenUiState.GetLoading
            try {
                when (val response = apiRepository.getProfile()) {
                    is Result.Error<MessageError> -> ProfileScreenUiState.Error(UserError(message = response.error.message))
                    is Result.Success<UserPayload> -> {
                        val user = response.data.user
                        updateUsername(user.username)
                        updateEmail(user.email)
                        avatarUrl = user.avatarUrl

                        profileScreenUiState = ProfileScreenUiState.Waiting
                    }
                }
            } catch (e: HttpException) {
                profileScreenUiState = ProfileScreenUiState.Error(UserError(message = "An unexpected error occurred"))
            }
        }
    }

    fun updateProfile(imageUri: Uri? = null, contentResolver: ContentResolver? = null) {
        viewModelScope.launch {
            profileScreenUiState = ProfileScreenUiState.UpdateLoading
            try {
                val response = apiRepository.updateProfile(
                    username = username,
                    email = email,
                    newPassword = if (newPassword != "") newPassword else null,
                    oldPassword,
                    imageUri,
                    contentResolver
                )
                profileScreenUiState = when (response) {
                    is Result.Error<UserError> -> ProfileScreenUiState.Error(response.error, loggedOut = response.loggedOut)
                    is Result.Success<UserPayload> -> ProfileScreenUiState.Success(response.data)
                }
            } catch (e: HttpException) {
                profileScreenUiState = ProfileScreenUiState.Error(UserError(message = "An unexpected error occurred"))
            }
        }
    }

    fun logoutUser(onLogout: () -> Unit) {
        viewModelScope.launch {
            profileScreenUiState = ProfileScreenUiState.LogoutLoading
            try {
                when (val response = apiRepository.logoutUser()) {
                    is Result.Error<TokensError> -> {
                        profileScreenUiState = ProfileScreenUiState.Error(UserError(message = "An unexpected error occurred"), loggedOut = response.loggedOut)
                    }
                    is Result.Success<MessagePayload> -> {
                        onLogout()
                    }
                }
            } catch (e: HttpException) {
                profileScreenUiState = ProfileScreenUiState.Error(UserError(message = "An unexpected error occurred"))
            }
        }
    }
}