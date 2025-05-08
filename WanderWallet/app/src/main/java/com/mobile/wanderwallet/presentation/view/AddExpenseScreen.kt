package com.mobile.wanderwallet.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobile.wanderwallet.data.model.Category
import com.mobile.wanderwallet.presentation.components.DatePickerField
import com.mobile.wanderwallet.presentation.components.FormField
import com.mobile.wanderwallet.presentation.components.RectangularButton
import com.mobile.wanderwallet.presentation.components.SelectCategoryTextField
import com.mobile.wanderwallet.presentation.viewmodel.AddExpenseScreenUiState
import com.mobile.wanderwallet.presentation.viewmodel.AddExpenseScreenViewModel

@Composable
fun AddExpenseScreen(
    onSuccess: () -> Unit,
    onLoggedOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddExpenseScreenViewModel = hiltViewModel()
) {
    val uiState = viewModel.addExpenseScreenUiState

    if ((uiState is AddExpenseScreenUiState.Error) && uiState.loggedOut) {
        onLoggedOut()
    }
    when (uiState) {
        is AddExpenseScreenUiState.Success -> {
            onSuccess()
        }
        else -> {
            AddExpenseScreenContent(
                uiState = uiState,
                name = viewModel.name,
                onNameChange = viewModel::updateName,
                amount = viewModel.amount,
                onAmountChange = viewModel::updateAmount,
                onCategoryChange = viewModel::updateCategory,
                date = viewModel.date,
                onDateChange = viewModel::updateDate,
                notes = viewModel.notes,
                onNotesChange = viewModel::updateNotes,
                onSubmit = viewModel::addExpense,
                modifier = modifier
            )
        }
    }
}

@Composable
fun AddExpenseScreenContent(
    uiState: AddExpenseScreenUiState,
    name: String,
    onNameChange: (String) -> Unit,
    amount: String,
    onAmountChange: (String) -> Unit,
    onCategoryChange: (Category) -> Unit,
    date: String,
    onDateChange: (String) -> Unit,
    notes: String?,
    onNotesChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(48.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            FormField(
                label = "Expense Name",
                placeholder = "Spaghetti at that Italian place",
                value = name,
                onValueChange = onNameChange,
                errorMessage = if (uiState is AddExpenseScreenUiState.Error) uiState.error.name else null,
            )
            FormField(
                label = "Amount",
                placeholder = "50",
                value = amount,
                onValueChange = onAmountChange,
                errorMessage = if (uiState is AddExpenseScreenUiState.Error) uiState.error.amount else null,
                isNumber = true
            )
            SelectCategoryTextField(
                label = "Category",
                onItemSelected = onCategoryChange
            )
            DatePickerField(
                label = "Date",
                selectedDate = date,
                onDateSelected = onDateChange,
                errorMessage = if (uiState is AddExpenseScreenUiState.Error) uiState.error.date else null
            )
            FormField(
                label = "Note",
                placeholder = "Add your note",
                value = notes ?: "",
                onValueChange = onNotesChange,
                isFinal = true
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState is AddExpenseScreenUiState.Error && uiState.error.message != null) {
                Text(uiState.error.message, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.error)
            }
            RectangularButton(
                onClick = onSubmit,
                enabled = uiState !is AddExpenseScreenUiState.Loading,
                contentPadding = PaddingValues(horizontal = 48.dp, vertical = 8.dp)
            ) {
                Text("Add Expense", modifier = Modifier.padding(vertical = 4.dp), style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}