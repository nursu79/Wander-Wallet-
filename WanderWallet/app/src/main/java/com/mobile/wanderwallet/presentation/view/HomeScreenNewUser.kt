package com.mobile.wanderwallet.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mobile.wanderwallet.R
import com.mobile.wanderwallet.data.model.User
import com.mobile.wanderwallet.ui.theme.WanderWalletTheme
import java.util.Date

@Composable
fun HomeScreenNewUser(
    user: User,
    currentScreen: MainContentScreen,
    navController: NavController,
    onUpButtonClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateToTripsScreen: () -> Unit,
    navigateToSummaryScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            MainContentAppBar(
                user = user,
                currentScreen = currentScreen,
                canNavigateBack = true,
                navigateUp = onUpButtonClick,
                onNotificationsClick = onNotificationsClick,
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("create_trip") }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create a new trip")
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Hi ${user.username} 👋️",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = currentScreen.title,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Image(
                painter = painterResource(id = R.drawable.welcome_illustration),
                contentDescription = "Welcome Illustration",
                modifier = Modifier.fillMaxWidth()
                .height(240.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Get started",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            currentScreen.subString?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(100.dp)) // Padding to prevent FAB overlap
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenNewUserPreview() {
    WanderWalletTheme {
        HomeScreenNewUser(
            user = User(
                id = "1",
                username = "John Doe",
                avatarUrl = "",
                email = "john@example.com",
                createdAt = Date(),
                updatedAt = Date(),
                notifications = emptyList()
            ),
            currentScreen = MainContentScreen.HomeScreenNewUser,
            navController = NavController(LocalContext.current),
            onUpButtonClick = {},
            onNotificationsClick = {},
            navigateToProfileScreen = {},
            navigateToTripsScreen = {},
            navigateToSummaryScreen = {}
        )
    }
}

