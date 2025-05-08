package com.mobile.wanderwallet.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobile.wanderwallet.data.model.Category
import com.mobile.wanderwallet.data.model.TripPayload
import com.mobile.wanderwallet.presentation.components.BudgetSummarySection
import com.mobile.wanderwallet.presentation.components.CategoryIcon
import com.mobile.wanderwallet.presentation.components.ExpenseCard
import com.mobile.wanderwallet.presentation.components.ExpensesByCategoriesCardsSection
import com.mobile.wanderwallet.presentation.components.Indicator
import com.mobile.wanderwallet.presentation.components.RectangularButton
import com.mobile.wanderwallet.presentation.viewmodel.TripDeleteState
import com.mobile.wanderwallet.presentation.viewmodel.TripDetailsScreenUiState
import com.mobile.wanderwallet.presentation.viewmodel.TripDetailsScreenViewModel
import com.mobile.wanderwallet.utils.getExpensesByCategory
import java.util.Locale

@Composable
fun TripDetailsScreen(
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onAddExpenseClick: () -> Unit,
    onExpenseClick: (String) -> Unit,
    onLoggedOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TripDetailsScreenViewModel = hiltViewModel()
) {
    val tripDeleteState = viewModel.tripDeleteState

    if (tripDeleteState is TripDeleteState.Success) {
        onDeleteClick()
    } else if (tripDeleteState is TripDeleteState.Error && tripDeleteState.loggedOut) {
        onLoggedOut()
    }
    when (val uiState = viewModel.tripDetailsScreenUiState) {
        is TripDetailsScreenUiState.Error -> {
            if (uiState.loggedOut) {
                onLoggedOut()
            } else {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = modifier
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(uiState.error.message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                        RectangularButton(
                            onClick = { viewModel.getTripDetails() },
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
        TripDetailsScreenUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Indicator(size = 48.dp)
            }
        }
        is TripDetailsScreenUiState.Success -> {
            TripDetailsScreenContent(
                onEditClick = onEditClick,
                deleteState = tripDeleteState,
                onDeleteClick = { viewModel.deleteTrip() },
                onAddExpenseClick = onAddExpenseClick,
                onExpenseClick = onExpenseClick,
                tripDetails = uiState.data,
                modifier = modifier
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailsScreenContent(
    onEditClick: () -> Unit,
    deleteState: TripDeleteState,
    onDeleteClick: () -> Unit,
    onAddExpenseClick: () -> Unit,
    onExpenseClick: (String) -> Unit,
    tripDetails: TripPayload,
    modifier: Modifier = Modifier
) {
    var selectedCategory: Category by rememberSaveable { mutableStateOf(Category.FOOD) }
    var isSheetOpen by rememberSaveable { mutableStateOf(false) }
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
            ) {
                BudgetSummarySection(
                    totalBudget = tripDetails.trip?.budget ?: 0f,
                    totalExpense = tripDetails.totalExpenditure ?: 0f
                )

                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            RectangularButton(
                                onClick = onEditClick,
                                modifier = Modifier.weight(0.4f),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Text("Edit", style = MaterialTheme.typography.bodyMedium)
                            }

                            Spacer(Modifier.width(16.dp))

                            RectangularButton(
                                onClick = onDeleteClick,
                                modifier = Modifier.weight(0.4f),
                                color = MaterialTheme.colorScheme.error,
                                enabled = (deleteState !is TripDeleteState.Loading)
                            ) {
                                Text("Delete", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        if (deleteState is TripDeleteState.Success) {
                            Text(deleteState.data.message, style = MaterialTheme.typography.bodySmall)
                        } else if (deleteState is TripDeleteState.Error) {
                            Text(deleteState.error.message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = "Expenses",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Spacer(Modifier.height(8.dp))
                ExpensesByCategoriesCardsSection(
                    expensesByCategories = tripDetails.expensesByCategory,
                    onCardClick = {
                        selectedCategory = it
                        isSheetOpen = true
                    }
                )
            }
        }
        FloatingActionButton(
            onClick = onAddExpenseClick,
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            shape = CircleShape,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                "Add"
            )
        }
    }

    if (isSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = {
                isSheetOpen = false
            }
        ) {
            val expenses = getExpensesByCategory(tripDetails.trip?.expenses, selectedCategory)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryIcon(selectedCategory)
                    Text(selectedCategory
                        .toString()
                        .lowercase()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(expenses, key = { expense -> expense.id }) { expense ->
                        ExpenseCard(
                            expense = expense,
                            onClick = onExpenseClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        )
                    }
                }
            }
        }
    }
}
