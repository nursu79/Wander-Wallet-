package com.mobile.wanderwallet.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.wanderwallet.data.model.MessageError
import com.mobile.wanderwallet.data.model.MessagePayload
import com.mobile.wanderwallet.data.model.NotificationsPayload
import com.mobile.wanderwallet.data.model.Result
import com.mobile.wanderwallet.data.repository.WanderWalletApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

sealed interface DeleteNotificationState {
    data class Success(val data: MessagePayload): DeleteNotificationState
    data class Error(val id: String, val data: MessageError, val loggedOut: Boolean = false): DeleteNotificationState
    data object Loading: DeleteNotificationState
    data object Waiting: DeleteNotificationState
}

sealed interface NotificationsScreenUiState {
    data class Success(val data: NotificationsPayload): NotificationsScreenUiState
    data class Error(val error: MessageError, val loggedOut: Boolean = false): NotificationsScreenUiState
    data object Loading: NotificationsScreenUiState
}

@HiltViewModel
class NotificationsScreenViewModel @Inject constructor(
    private val apiRepository: WanderWalletApiRepository
): ViewModel() {
    var notificationsScreenUiState: NotificationsScreenUiState by mutableStateOf(NotificationsScreenUiState.Loading)
    var deleteNotificationState: DeleteNotificationState by mutableStateOf(DeleteNotificationState.Waiting)

    init {
        getNotifications()
    }

    fun getNotifications() {
        viewModelScope.launch {
            notificationsScreenUiState = NotificationsScreenUiState.Loading
            try {
                val response = apiRepository.getNotifications()

                notificationsScreenUiState = when (response) {
                    is Result.Error<MessageError> -> NotificationsScreenUiState.Error(response.error, loggedOut = response.loggedOut)
                    is Result.Success<NotificationsPayload> -> NotificationsScreenUiState.Success(response.data)
                }
            } catch (e: HttpException) {
                notificationsScreenUiState = NotificationsScreenUiState.Error(MessageError(message = "An unexpected error occurred"))
            }
        }
    }

    fun deleteNotification(id: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            deleteNotificationState = DeleteNotificationState.Loading
            try {
                when (val response = apiRepository.deleteNotifications(id)) {
                    is Result.Error<MessageError> -> {
                        deleteNotificationState = DeleteNotificationState.Error(id, response.error, loggedOut = response.loggedOut)
                    }
                    is Result.Success<MessagePayload> -> {
                        deleteNotificationState = DeleteNotificationState.Success(response.data)
                        onSuccess()
                    }
                }
            } catch (e: HttpException) {
                deleteNotificationState = DeleteNotificationState.Error(id, MessageError(message = "An unexpected error occurred"))
            }
        }
    }
}