package com.mobile.wanderwallet.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobile.wanderwallet.data.model.AllStats
import com.mobile.wanderwallet.presentation.components.AvgSpendingCard
import com.mobile.wanderwallet.presentation.components.BudgetComparisonsCard
import com.mobile.wanderwallet.presentation.components.Indicator
import com.mobile.wanderwallet.presentation.components.LeastExpensiveTripCard
import com.mobile.wanderwallet.presentation.components.MostExpensiveTripCard
import com.mobile.wanderwallet.presentation.components.RectangularButton
import com.mobile.wanderwallet.presentation.components.SpendingByCategoryCard
import com.mobile.wanderwallet.presentation.components.SpendingByMonthCard
import com.mobile.wanderwallet.presentation.components.TotalSpendingCard
import com.mobile.wanderwallet.presentation.viewmodel.SummaryScreenUiState
import com.mobile.wanderwallet.presentation.viewmodel.SummaryScreenViewModel

@Composable
fun SummaryScreen(
    onTripClick: (String) -> Unit,
    onLoggedOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SummaryScreenViewModel = hiltViewModel()
) {
    when (val uiState = viewModel.summaryScreenUiState) {
        is SummaryScreenUiState.Error -> {
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
                            onClick = { viewModel.getStats() },
                        ) {
                            Text(
                                "Retry",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
        SummaryScreenUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Indicator(size = 48.dp)
            }
        }
        is SummaryScreenUiState.Success -> {
            SummaryScreenContent(
                stats = uiState.data,
                onTripClick = onTripClick,
                modifier = modifier
            )
        }
    }
}

@Composable
fun SummaryScreenContent(
    stats: AllStats,
    onTripClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(32.dp),
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        TotalSpendingCard(
            totalBudget = stats.totalBudget,
            totalSpending = stats.totalSpending
        )

        AvgSpendingCard(
            avgPerDay = stats.avgSpendingPerDay,
            avgPerTrip = stats.avgSpendingPerTrip
        )

        SpendingByCategoryCard(
            spendingByCategory = stats.spendingByCategory
        )

        SpendingByMonthCard(monthlySpending = stats.monthlySpending)

        BudgetComparisonsCard(
            budgetComparisons = stats.budgetComparisons,
            onTripClick = onTripClick
        )

        MostExpensiveTripCard(
            trip = stats.mostExpensiveTrip,
            onTripClick = onTripClick
        )

        LeastExpensiveTripCard(
            trip = stats.leastExpensiveTrip,
            onTripClick = onTripClick
        )
    }
}
