package com.mobile.wanderwallet.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobile.wanderwallet.data.model.Notification
import com.mobile.wanderwallet.presentation.components.Indicator
import com.mobile.wanderwallet.presentation.components.NotificationCard
import com.mobile.wanderwallet.presentation.components.RectangularButton
import com.mobile.wanderwallet.presentation.viewmodel.DeleteNotificationState
import com.mobile.wanderwallet.presentation.viewmodel.NotificationsScreenUiState
import com.mobile.wanderwallet.presentation.viewmodel.NotificationsScreenViewModel

@Composable
fun NotificationsScreen(
    onViewDetailsClick: (String) -> Unit,
    onDismissClick: () -> Unit,
    onLoggedOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotificationsScreenViewModel = hiltViewModel()
) {
    when (val uiState = viewModel.notificationsScreenUiState) {
        is NotificationsScreenUiState.Error -> {
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
                            onClick = { viewModel.getNotifications() },
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
        NotificationsScreenUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Indicator(size = 48.dp)
            }
        }
        is NotificationsScreenUiState.Success -> {
            com.mobile.wanderwallet.presentation.viewmodel.NotificationsScreenContent(
                notifications = uiState.data.notifications,
                deleteState = viewModel.deleteNotificationState,
                onViewDetailsClick = onViewDetailsClick,
                onDismissClick = { id ->
                    viewModel.deleteNotification(id, onDismissClick)
                },
                onLoggedOut = onLoggedOut,
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun NotificationsScreenContent(
    notifications: List<Notification>,
    deleteState: DeleteNotificationState,
    onViewDetailsClick: (String) -> Unit,
    onDismissClick: (String) -> Unit,
    onLoggedOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (deleteState is DeleteNotificationState.Error && deleteState.loggedOut) {
        onLoggedOut()
    } else {
        if (notifications.isEmpty()) {
            Box(contentAlignment = Alignment.TopCenter, modifier = modifier) {
                Text(
                    text = "Nothing to see here",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
        }
        LazyColumn(
            modifier = modifier
        ) {
            items(notifications) { notification ->
                NotificationCard(
                    notification = notification,
                    onDismissClick = { onDismissClick(notification.id) },
                    onViewDetailsClick = { onViewDetailsClick(notification.tripId) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                )
            }
        }
    }
}
