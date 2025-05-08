package com.mobile.wanderwallet.presentation.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.text.input.PasswordVisualTransformation

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mobile.wanderwallet.R
import com.mobile.wanderwallet.data.model.Notification

import com.mobile.wanderwallet.data.model.User
import com.mobile.wanderwallet.presentation.components.*
import com.mobile.wanderwallet.ui.theme.WanderWalletTheme
import java.util.*

@Composable
fun CreateTripScreen(
    user: User,
    currentScreen: MainContentScreen,
    onUpButtonClick: () -> Unit,
    onSaveClick: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateToTripsScreen: () -> Unit,
    navigateToSummaryScreen: () -> Unit,
    notifications: List<Notification>,
    modifier: Modifier = Modifier
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var tripName by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> imageUri = uri }
    )

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

            // Image Upload
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
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
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = {
                    imageLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }) {
                    Text("Upload Trip Image")
                }
            }



            // Form Fields
            FormField(
                "Trip Name",
                "Enter trip name",
                tripName,
                { tripName = it },
                null,

                )
            FormField(
                "Destination",
                "Enter destination",
                destination,
                { destination = it },
                null,

                )
            FormField(
                "Budget",
                "Set budget",
                budget,
                { budget = it },
                errorMessage = null,
                isNumber = true,

                )

            Spacer(modifier = Modifier.height(16.dp))

            // Date Pickers in Row
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

            Spacer(modifier = Modifier.height(16.dp))

            // Buttons
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
                    Text("Create Trip")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview
@Composable
fun CreateTripScreenPreview() {
    WanderWalletTheme {
        CreateTripScreen(
            user = User(
                id = "1",
                username = "John Doe",
                avatarUrl = "",
                email = "",
                createdAt = Date(),
                updatedAt = Date(),
                notifications = emptyList()
            ),
            currentScreen = MainContentScreen.CreateTripScreen,
            onUpButtonClick = {},
            onSaveClick = {},
            navigateToProfileScreen = {},
            navigateToTripsScreen = {},
            notifications = emptyList(),
            navigateToSummaryScreen = {}
        )
    }
}
