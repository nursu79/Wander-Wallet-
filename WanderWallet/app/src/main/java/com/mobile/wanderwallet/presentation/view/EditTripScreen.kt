package com.mobile.wanderwallet.presentation.view

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mobile.wanderwallet.R
import com.mobile.wanderwallet.data.model.User
import com.mobile.wanderwallet.presentation.components.*
import com.mobile.wanderwallet.ui.theme.WanderWalletTheme
import java.util.*

@Composable
fun EditTripScreen(
    user: User,
    currentScreen: MainContentScreen,
    onUpButtonClick: () -> Unit,
    onSaveClick: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateToTripsScreen: () -> Unit,
    navigateToSummaryScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imageUri by remember { mutableStateOf<Uri?>(null) }
    var tripName by remember { mutableStateOf("Trip to Japan") }
    var destination by remember { mutableStateOf("Tokyo") }
    var budget by remember { mutableStateOf("3000") }
    var startDate by remember { mutableStateOf("2024-05-10") }
    var endDate by remember { mutableStateOf("2024-05-20") }

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
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Trip image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                    }
                    else {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_placeholder),
                            contentDescription = "Trip Image Placeholder",
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("Trip Image (Editing)", style = MaterialTheme.typography.labelMedium)
            }



            FormField(
                "Trip Name",
                "Edit trip name",
                tripName,
                { tripName = it },
                null,

                )
            FormField(
                "Destination",
                "Edit destination",
                destination,
                { destination = it },
                null,

                )
            FormField(
                "Budget",
                "Edit budget",
                budget,
                { budget = it },
                errorMessage = null,
                isNumber = true,

                )
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    DatePickerField("Start Date", startDate, { startDate = it })
                }
                Box(modifier = Modifier.weight(1f)) {
                    DatePickerField("End Date", endDate, { endDate = it })
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                RectangularButton(
                    onClick = { /* Cancel logic */ },
                    color = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
                RectangularButton(
                    onClick = onSaveClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save Changes")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview
@Composable
fun EditTripScreenPreview() {
    WanderWalletTheme {
        EditTripScreen(
            user = User(
                id = "2",
                username = "Jane Smith",
                avatarUrl = "",
                email = "",
                createdAt = Date(),
                updatedAt = Date(),
                notifications = emptyList()
            ),
            currentScreen = MainContentScreen.EditTripScreen,
            onUpButtonClick = {},
            onSaveClick = {},
            navigateToProfileScreen = {},
            navigateToTripsScreen = {},
            navigateToSummaryScreen = {}
        )
    }
}
