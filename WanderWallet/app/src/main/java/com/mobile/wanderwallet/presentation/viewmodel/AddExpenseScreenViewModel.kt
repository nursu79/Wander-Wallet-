package com.mobile.wanderwallet.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.wanderwallet.data.model.Category
import com.mobile.wanderwallet.data.model.ExpenseError
import com.mobile.wanderwallet.data.model.ExpensePayload
import com.mobile.wanderwallet.data.model.Result
import com.mobile.wanderwallet.data.repository.WanderWalletApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

sealed interface AddExpenseScreenUiState {
    data class Success(val data: ExpensePayload): AddExpenseScreenUiState
    data class Error(val error: ExpenseError, val loggedOut: Boolean = false): AddExpenseScreenUiState
    data object Loading: AddExpenseScreenUiState
    data object Waiting: AddExpenseScreenUiState
}

@HiltViewModel
class AddExpenseScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val apiRepository: WanderWalletApiRepository
): ViewModel() {
    private val tripId = savedStateHandle.get<String>("tripId") ?: ""
    var addExpenseScreenUiState: AddExpenseScreenUiState by mutableStateOf(AddExpenseScreenUiState.Waiting)

    var name by mutableStateOf("")
        private set

    var amount by mutableStateOf("")
        private set

    var category: Category by mutableStateOf(Category.FOOD)
        private set

    var date by mutableStateOf("")
        private set

    var notes: String by mutableStateOf("")
        private set

    fun updateName(value: String) {
        name = value
    }

    fun updateAmount(value: String) {
        amount = value
    }

    fun updateCategory(value: Category) {
        category = value
    }

    fun updateDate(value: String) {
        date = value
    }

    fun updateNotes(value: String) {
        notes = value
    }

    fun addExpense() {
        viewModelScope.launch {
            addExpenseScreenUiState = AddExpenseScreenUiState.Loading
            try {
                val response = apiRepository.createExpense(
                    tripId = tripId,
                    name = name,
                    amount = amount,
                    category = category,
                    date = date,
                    notes = if (notes == "") null else notes
                )

                addExpenseScreenUiState = when (response) {
                    is Result.Error<ExpenseError> -> AddExpenseScreenUiState.Error(response.error, loggedOut = response.loggedOut)
                    is Result.Success<ExpensePayload> -> AddExpenseScreenUiState.Success(response.data)
                }
            } catch (e: HttpException) {
                addExpenseScreenUiState = AddExpenseScreenUiState.Error(ExpenseError(message = "An unexpected error occurred"))
            }
        }
    }
}