package com.mobile.wanderwallet.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.HourglassTop
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobile.wanderwallet.presentation.components.Indicator
import com.mobile.wanderwallet.presentation.components.RectangularButton
import com.mobile.wanderwallet.presentation.components.TripCard
import com.mobile.wanderwallet.presentation.viewmodel.Timeframe
import com.mobile.wanderwallet.presentation.viewmodel.TripsScreenUiState
import com.mobile.wanderwallet.presentation.viewmodel.TripsScreenViewModel

data class TabItem(
    val timeframe: Timeframe,
    val title: String,
    val unSelectedIcon: ImageVector,
    val selectedIcon: ImageVector
)

@Composable
fun TripsScreen(
    onTripClick: (String) -> Unit,
    onLoggedOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TripsScreenViewModel = hiltViewModel()
) {
    when (val uiState = viewModel.tripsScreenUiState) {
        is TripsScreenUiState.Error -> {
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
                            onClick = { viewModel.getTrips() },
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
        else -> {
            TripsScreenContent(
                uiState = uiState,
                currentTimeframe = viewModel.timeframe,
                onTimeframeClick = { viewModel.updateTimeframe(it) },
                onTripClick = onTripClick,
                modifier = modifier
            )
        }
    }
}

@Composable
fun TripsScreenContent(
    uiState: TripsScreenUiState,
    currentTimeframe: Timeframe,
    onTimeframeClick: (Timeframe) -> Unit,
    onTripClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabItems = listOf(
        TabItem(timeframe = Timeframe.Past, title = Timeframe.Past.name, unSelectedIcon = Icons.Outlined.Archive, selectedIcon = Icons.Filled.Archive),
        TabItem(timeframe = Timeframe.Current, title = Timeframe.Current.name, unSelectedIcon = Icons.Outlined.PlayCircle, selectedIcon = Icons.Filled.PlayCircle),
        TabItem(timeframe = Timeframe.Pending, title = Timeframe.Pending.name, unSelectedIcon = Icons.Outlined.HourglassTop, selectedIcon = Icons.Filled.HourglassTop)

    )
    Column(
        verticalArrangement = Arrangement.spacedBy(32.dp),
        modifier = modifier
    ) {
        val pagerState = rememberPagerState {
            tabItems.size
        }
        LaunchedEffect(currentTimeframe) {
            pagerState.animateScrollToPage(currentTimeframe.index)
        }
        LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
            if (!pagerState.isScrollInProgress) {
                onTimeframeClick(
                    when (pagerState.currentPage) {
                        Timeframe.Past.index -> Timeframe.Past
                        Timeframe.Pending.index -> Timeframe.Pending
                        else -> Timeframe.Current
                    }
                )
            }
        }
        TabRow(selectedTabIndex = currentTimeframe.index) {
            tabItems.forEach { item ->
                Tab(
                    selected = (item.timeframe.index == currentTimeframe.index),
                    onClick = {
                        onTimeframeClick(item.timeframe)
                    },
                    text = {
                        Text(text = item.title, style = MaterialTheme.typography.labelLarge)
                    },
                    icon = {
                        Icon(
                            imageVector = if (item.timeframe.index == currentTimeframe.index) item.selectedIcon else item.unSelectedIcon,
                            contentDescription = item.title
                        )
                    }
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (uiState) {
                is TripsScreenUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(uiState.error.message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                    }
                }
                TripsScreenUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Indicator(size = 48.dp)
                    }
                }
                is TripsScreenUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.data.trips) { trip ->
                            TripCard(
                                trip = trip,
                                onClick = onTripClick,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
