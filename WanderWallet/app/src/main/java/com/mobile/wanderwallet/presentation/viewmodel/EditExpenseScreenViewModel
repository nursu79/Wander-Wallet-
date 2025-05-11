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
import com.mobile.wanderwallet.data.model.MessageError
import com.mobile.wanderwallet.data.model.Result
import com.mobile.wanderwallet.data.repository.WanderWalletApiRepository
import com.mobile.wanderwallet.utils.convertDateToFormattedString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

sealed interface EditExpenseScreenUiState {
    data class Success(val data: ExpensePayload): EditExpenseScreenUiState
    data class Error(val error: ExpenseError, val loggedOut: Boolean = false): EditExpenseScreenUiState
    data object GetLoading: EditExpenseScreenUiState
    data object UpdateLoading: EditExpenseScreenUiState
    data object Waiting: EditExpenseScreenUiState
}

@HiltViewModel
class EditExpenseScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val apiRepository: WanderWalletApiRepository
): ViewModel() {
    private val expenseId = savedStateHandle.get<String>("expenseId") ?: ""
    var editExpenseScreenUiState: EditExpenseScreenUiState by mutableStateOf(EditExpenseScreenUiState.GetLoading)

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

    init {
        getExpense()
    }

    private fun getExpense() {
        viewModelScope.launch {
            editExpenseScreenUiState = EditExpenseScreenUiState.GetLoading
            try {
                when (val response = apiRepository.getExpense(expenseId)) {
                    is Result.Error<MessageError> -> {
                        editExpenseScreenUiState = EditExpenseScreenUiState.Error(ExpenseError(message = response.error.message), loggedOut = response.loggedOut)
                    }
                    is Result.Success<ExpensePayload> -> {
                        val expense = response.data.expense

                        updateName(expense.name)
                        updateAmount(expense.amount.toString())
                        updateCategory(expense.category)
                        updateDate(convertDateToFormattedString(expense.date) ?: "")
                        updateNotes(expense.notes ?: "")
                        editExpenseScreenUiState = EditExpenseScreenUiState.Waiting
                    }
                }
            } catch (e: HttpException) {
                editExpenseScreenUiState = EditExpenseScreenUiState.Error(
                    ExpenseError(message = "An unexpected error occurred")
                )
            }
        }
    }

    fun updateExpense() {
        viewModelScope.launch {
            editExpenseScreenUiState = EditExpenseScreenUiState.UpdateLoading
            try {
                val response = apiRepository.updateExpense(
                    id = expenseId,
                    name = name,
                    amount = amount,
                    category = category,
                    date = date,
                    notes = if (notes == "") null else notes
                )

                editExpenseScreenUiState = when (response) {
                    is Result.Error<ExpenseError> -> EditExpenseScreenUiState.Error(response.error, loggedOut = response.loggedOut)
                    is Result.Success<ExpensePayload> -> EditExpenseScreenUiState.Success(response.data)
                }
            } catch (e: HttpException) {
                editExpenseScreenUiState = EditExpenseScreenUiState.Error(ExpenseError(message = "An unexpected error occurred"))
            }
        }
    }
}
