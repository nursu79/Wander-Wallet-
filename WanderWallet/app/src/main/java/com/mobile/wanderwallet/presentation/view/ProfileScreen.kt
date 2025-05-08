package com.mobile.wanderwallet.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.mobile.wanderwallet.R
import com.mobile.wanderwallet.data.model.User
import com.mobile.wanderwallet.presentation.components.FormField
import com.mobile.wanderwallet.ui.theme.WanderWalletTheme
import java.util.*

@Composable
fun ProfileScreen(
    user: User,
    currentScreen: MainContentScreen,
    onLogoutClick: () -> Unit,
    navigateToTripsScreen: () -> Unit,
    navigateToSummaryScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit
) {
    var name by remember { mutableStateOf(user.username) }
    var email by remember { mutableStateOf(user.email) }
    var password by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            MainContentAppBar(
                user = user,
                currentScreen = currentScreen,
                canNavigateBack = false,
                navigateUp = {},
                modifier = Modifier.height(160.dp)
            )
        },
        bottomBar = {
            MainContentBottomBar(
                currentScreen = currentScreen,
                navigateToTripsScreen = navigateToTripsScreen,
                navigateToSummaryScreen = navigateToSummaryScreen,
                navigateToProfileScreen = navigateToProfileScreen
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Avatar
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = user.avatarUrl),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Edit Profile", style = MaterialTheme.typography.titleSmall)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Form fields
            FormField(
                label = "Name",
                placeholder = "Your name",
                value = name,
                onValueChange = { name = it },
                errorMessage = null
            )
            Spacer(modifier = Modifier.height(24.dp))
            FormField(
                label = "Email",
                placeholder = "Your email",
                value = email,
                onValueChange = { email = it },
                errorMessage = null
            )
            Spacer(modifier = Modifier.height(24.dp))
            FormField(
                label = "Password",
                placeholder = " Update Password",
                value = password,
                onValueChange = { password = it },
                errorMessage = null,
                isPassword = true,
                isFinal = true
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Logout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(onClick = onLogoutClick) {
                    Icon(Icons.Default.Logout, contentDescription = "Logout")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Log out", style = MaterialTheme.typography.labelLarge)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    WanderWalletTheme {
        ProfileScreen(
            user = User(
                id = "1",
                username = "Alice Johnson",
                avatarUrl = "https://randomuser.me/api/portraits/women/1.jpg",
                email = "alice@example.com",
                createdAt = Date(),
                updatedAt = Date(),
                notifications = emptyList()

            ),
            currentScreen = MainContentScreen.ProfileScreen,
            onLogoutClick = {},
            navigateToTripsScreen = {},
            navigateToSummaryScreen = {},
            navigateToProfileScreen = {}
        )
    }
}
