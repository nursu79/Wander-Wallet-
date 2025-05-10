package com.mobile.wanderwallet.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import com.mobile.wanderwallet.presentation.components.Indicator
import com.mobile.wanderwallet.presentation.components.RectangularButton
import com.mobile.wanderwallet.presentation.components.SelectCategoryTextField
import com.mobile.wanderwallet.presentation.viewmodel.EditExpenseScreenUiState
import com.mobile.wanderwallet.presentation.viewmodel.EditExpenseScreenViewModel

@Composable
fun EditExpenseScreen(
    onSuccess: () -> Unit,
    onCancel: () -> Unit,
    onLoggedOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditExpenseScreenViewModel = hiltViewModel()
) {
    val uiState = viewModel.editExpenseScreenUiState

    if (uiState is EditExpenseScreenUiState.Error && uiState.loggedOut) {
        onLoggedOut()
    }

    when (uiState) {
        is EditExpenseScreenUiState.Success -> {
            onSuccess()
        }
        is EditExpenseScreenUiState.GetLoading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Indicator(size = 48.dp)
            }
        }
        else -> {
            EditExpenseScreenContent(
                uiState = uiState,
                name = viewModel.name,
                onNameChange = viewModel::updateName,
                amount = viewModel.amount,
                onAmountChange = viewModel::updateAmount,
                category = viewModel.category,
                onCategoryChange = viewModel::updateCategory,
                date = viewModel.date,
                onDateChange = viewModel::updateDate,
                notes = viewModel.notes,
                onNotesChange = viewModel::updateNotes,
                onSubmit = viewModel::updateExpense,
                onCancel = onCancel,
                modifier = modifier
            )
        }
    }
}

@Composable
fun EditExpenseScreenContent(
    uiState: EditExpenseScreenUiState,
    name: String,
    onNameChange: (String) -> Unit,
    amount: String,
    onAmountChange: (String) -> Unit,
    category: Category,
    onCategoryChange: (Category) -> Unit,
    date: String,
    onDateChange: (String) -> Unit,
    notes: String?,
    onNotesChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
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
                errorMessage = if (uiState is EditExpenseScreenUiState.Error) uiState.error.name else null,
            )
            FormField(
                label = "Amount",
                placeholder = "50",
                value = amount,
                onValueChange = onAmountChange,
                errorMessage = if (uiState is EditExpenseScreenUiState.Error) uiState.error.amount else null,
                isNumber = true
            )
            SelectCategoryTextField(
                label = "Category",
                selectedCategory = category,
                onItemSelected = onCategoryChange
            )
            DatePickerField(
                label = "Date",
                selectedDate = date,
                onDateSelected = onDateChange,
                errorMessage = if (uiState is EditExpenseScreenUiState.Error) uiState.error.date else null
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
            if (uiState is EditExpenseScreenUiState.Error && uiState.error.message != null) {
                Text(uiState.error.message, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.error)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                RectangularButton(
                    onClick = onCancel,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onError)
                }
                RectangularButton(
                    onClick = onSubmit,
                    enabled = uiState !is EditExpenseScreenUiState.UpdateLoading,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Update", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}


//@Composable
//fun EditExpense() {
//
//    var title by remember {
//        mutableStateOf("")
//    }
//    var amount by remember {
//        mutableStateOf("")
//    }
//    var note by remember {
//        mutableStateOf("")
//    }
//    var titleError by remember { mutableStateOf<String?>(null) }
//    var amountError by remember { mutableStateOf<String?>(null) }
//    var noteError by remember { mutableStateOf<String?>(null) }
//    var selectedCurrency by remember { mutableStateOf<String?>(null) }
//    var selectedDate by remember { mutableStateOf("") }
//    val currencies = listOf("USD", "EUR", "GBP", "JPY", "CAD")
//    var selectedCategory by remember { mutableStateOf<String?>(null) }
//    val categories = listOf("Food", "Transport", "Accommodation", "Entertainment", "Other")
//
//    Column(modifier = Modifier.padding(16.dp)) {
//        FormField(
//            "Expense Title",
//            "Pizza",
//             title,
//            onValueChange = { title = it},
//            errorMessage = titleError
//        )
//        Spacer(Modifier.height(16.dp))
//        FormField(
//            "Amount",
//            "0.0",
//            amount,
//            onValueChange = { amount = it},
//            errorMessage = amountError
//        )
//        Spacer(Modifier.height(16.dp))
//        Dropdown(
//            items = currencies,
//            selectedValue = selectedCurrency,
//            onValueSelected = { selectedCurrency = it },
//            label = "Currency",
//            placeholder = "USD",
//            modifier = Modifier.fillMaxWidth()
//        )
//        Spacer(Modifier.height(16.dp))
//        Dropdown(
//            items = categories,
//            selectedValue = selectedCategory,
//            onValueSelected = { selectedCategory = it },
//            label = "Category",
//            placeholder = "choose currency",
//            modifier = Modifier.fillMaxWidth()
//        )
//        Spacer(Modifier.height(16.dp))
//        DatePickerField(
//            label = "Date",
//            selectedDate = selectedDate,
//            onDateSelected = { newDate ->
//                selectedDate = newDate
//            },
//            modifier = Modifier.fillMaxWidth()
//        )
//        Spacer(Modifier.height(16.dp))
//        FormField(
//            "Note",
//            "Add your note",
//            note,
//            onValueChange = { title = it},
//            errorMessage = noteError
//        )
//        Spacer(Modifier.height(16.dp))
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 32.dp, vertical = 16.dp),
//            horizontalArrangement = Arrangement.Center,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            RectangularButton(
//                onClick = {},
//                modifier = Modifier.weight(0.3f),
//                color = Color(0xFF449494)
//            ) {
//                Text("Cancel")
//            }
//
//            Spacer(Modifier.width(16.dp))
//
//            RectangularButton(
//                onClick = {},
//                modifier = Modifier.weight(0.3f),
//                color = Color(0xFF449494)
//            ) {
//                Text("Save")
//            }
//        }
//    }
//}
//@Preview(showBackground = true)
//@Composable
//fun EditExpensePreview() {
//    EditExpense()
//}
