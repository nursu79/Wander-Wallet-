package com.mobile.wanderwallet.presentation.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.mobile.wanderwallet.data.model.User
import com.mobile.wanderwallet.presentation.components.NotificationCard
import com.mobile.wanderwallet.ui.theme.WanderWalletTheme
import java.util.*

@Composable
fun NotificationScreen(
    user: User,
    currentScreen: MainContentScreen,
    onUpButtonClick: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateToTripsScreen: () -> Unit,
    navigateToSummaryScreen: () -> Unit,
    onViewDetailsClick: () -> Unit,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            MainContentAppBar(
                user = user,
                currentScreen = currentScreen,
                canNavigateBack = true,
                navigateUp = onUpButtonClick,
                modifier = Modifier.height(160.dp)
            )
        },
        bottomBar = {
            MainContentBottomBar(
                currentScreen = currentScreen,
                navigateToProfileScreen = navigateToProfileScreen,
                navigateToTripsScreen = navigateToTripsScreen,
                navigateToSummaryScreen = navigateToSummaryScreen
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            repeat(5) {
                NotificationCard(
                    title = "Budget Exceeded",
                    message = "Your trip to Tokyo has exceeded the budget by \$200.",
                    onDismissClick = onDismissClick,
                    onViewDetailsClick = onViewDetailsClick
                )
            }
        }
    }
}

@Preview
@Composable
fun NotificationScreenPreview() {
    WanderWalletTheme {
        NotificationScreen(
            user = User(
                id = "1",
                username = "John Doe",
                avatarUrl = "",
                email = "john@example.com",
                createdAt = Date(),
                updatedAt = Date(),
                notifications = emptyList()
            ),
            currentScreen = MainContentScreen.NotificationScreen,
            onUpButtonClick = {},
            navigateToProfileScreen = {},
            navigateToTripsScreen = {},
            navigateToSummaryScreen = {},
            onViewDetailsClick = {},
            onDismissClick = {}
        )
    }
}
