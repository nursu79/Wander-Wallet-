package com.mobile.wanderwallet.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobile.wanderwallet.data.model.Expense
import com.mobile.wanderwallet.presentation.components.ExpenseDetailsCard
import com.mobile.wanderwallet.presentation.components.Indicator
import com.mobile.wanderwallet.presentation.components.RectangularButton
import com.mobile.wanderwallet.presentation.viewmodel.DeleteExpenseState
import com.mobile.wanderwallet.presentation.viewmodel.ExpenseDetailsScreenUiState
import com.mobile.wanderwallet.presentation.viewmodel.ExpenseDetailsScreenViewModel

@Composable
fun ExpenseDetailsScreen(
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onLoggedOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExpenseDetailsScreenViewModel = hiltViewModel()
) {
    val uiState = viewModel.expenseDetailsScreenUiState
    val deleteState = viewModel.deleteExpenseState

    if (deleteState is DeleteExpenseState.Error && deleteState.loggedOut) {
        onLoggedOut()
    } else if (deleteState is DeleteExpenseState.Success) {
        onDeleteClick()
    }

    when (uiState) {
        is ExpenseDetailsScreenUiState.Error -> {
            if (uiState.loggedOut) {
                onLoggedOut()
            }
            else {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = modifier
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            uiState.error.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        RectangularButton(
                            onClick = { viewModel.getExpenseDetails() },
                        ) {
                            Text(
                                "Retry",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
        ExpenseDetailsScreenUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Indicator(size = 48.dp)
            }
        }
        is ExpenseDetailsScreenUiState.Success -> {
            ExpenseDetailsScreenContent(
                expense = uiState.data.expense,
                deleteState = deleteState,
                onEditClick = onEditClick,
                onDeleteClick = viewModel::deleteExpense,
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun ExpenseDetailsScreenContent(
    expense: Expense,
    deleteState: DeleteExpenseState,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(32.dp),
        modifier = modifier
    ) {
        ExpenseDetailsCard(
            expense = expense,
            modifier = Modifier
                .fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RectangularButton(
                onClick = onEditClick,
                modifier = Modifier.weight(1f)
            ) {
                Text("Edit", style = MaterialTheme.typography.bodyMedium)
            }
            RectangularButton(
                onClick = onDeleteClick,
                color = MaterialTheme.colorScheme.error,
                enabled = deleteState !is DeleteExpenseState.Loading,
                modifier = Modifier.weight(1f)
            ) {
                Text("Delete", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
