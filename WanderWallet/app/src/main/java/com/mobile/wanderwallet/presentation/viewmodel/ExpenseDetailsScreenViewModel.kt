package com.mobile.wanderwallet.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.wanderwallet.data.model.ExpensePayload
import com.mobile.wanderwallet.data.model.MessageError
import com.mobile.wanderwallet.data.model.MessagePayload
import com.mobile.wanderwallet.data.model.Result
import com.mobile.wanderwallet.data.repository.WanderWalletApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

sealed interface ExpenseDetailsScreenUiState {
    data class Success(val data: ExpensePayload): ExpenseDetailsScreenUiState
    data class Error(val error: MessageError, val loggedOut: Boolean = false): ExpenseDetailsScreenUiState
    data object Loading: ExpenseDetailsScreenUiState
}

sealed interface DeleteExpenseState {
    data class Success(val data: MessagePayload): DeleteExpenseState
    data class Error(val error: MessageError, val loggedOut: Boolean = false): DeleteExpenseState
    data object Loading: DeleteExpenseState
    data object Waiting: DeleteExpenseState
}

@HiltViewModel
class ExpenseDetailsScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val apiRepository: WanderWalletApiRepository
): ViewModel() {
    var expenseDetailsScreenUiState: ExpenseDetailsScreenUiState by mutableStateOf(ExpenseDetailsScreenUiState.Loading)
    var deleteExpenseState: DeleteExpenseState by mutableStateOf(DeleteExpenseState.Waiting)
    private val expenseId = savedStateHandle.get<String>("expenseId") ?: ""

    init {
        getExpenseDetails()
    }

    fun getExpenseDetails() {
        viewModelScope.launch {
            expenseDetailsScreenUiState = ExpenseDetailsScreenUiState.Loading
            try {
                val response = apiRepository.getExpense(expenseId)

                expenseDetailsScreenUiState = when (response) {
                    is Result.Error<MessageError> -> ExpenseDetailsScreenUiState.Error(response.error, loggedOut = response.loggedOut)
                    is Result.Success<ExpensePayload> -> ExpenseDetailsScreenUiState.Success(response.data)
                }
            } catch (e: HttpException) {
                expenseDetailsScreenUiState = ExpenseDetailsScreenUiState.Error(MessageError(message = "An unexpected error occurred"))
            }
        }
    }

    fun deleteExpense() {
        viewModelScope.launch {
            deleteExpenseState = DeleteExpenseState.Loading
            try {
                val response = apiRepository.deleteExpense(expenseId)

                deleteExpenseState = when (response) {
                    is Result.Error<MessageError> -> DeleteExpenseState.Error(response.error, loggedOut = response.loggedOut)
                    is Result.Success<MessagePayload> -> DeleteExpenseState.Success(response.data)
                }
            } catch (e: HttpException) {
                deleteExpenseState = DeleteExpenseState.Error(MessageError("An unexpected error occurred"))
            }
        }
    }
}