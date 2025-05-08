package com.mobile.wanderwallet.presentation.view

import android.content.ContentResolver
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mobile.wanderwallet.R
import com.mobile.wanderwallet.presentation.components.DatePickerField
import com.mobile.wanderwallet.presentation.components.FormField
import com.mobile.wanderwallet.presentation.components.RectangularButton
import com.mobile.wanderwallet.presentation.viewmodel.CreateTripScreenUiState
import com.mobile.wanderwallet.presentation.viewmodel.CreateTripScreenViewModel

@Composable
fun CreateTripScreen(
    onSuccess: (String) -> Unit,
    onLoggedOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateTripScreenViewModel = hiltViewModel()
) {
    val uiState = viewModel.createTripScreenUiState

    if ((uiState is CreateTripScreenUiState.Error) && uiState.loggedOut) {
        onLoggedOut()
    }

    if (uiState is CreateTripScreenUiState.Success) {
        onSuccess(uiState.data.trip?.id ?: "")
    }

    CreateTripScreenContent(
        uiState = uiState,
        name = viewModel.name,
        onNameChange = viewModel::updateName,
        destination = viewModel.destination,
        onDestinationChange = viewModel::updateDestination,
        budget = viewModel.budget,
        onBudgetChange = viewModel::updateBudget,
        startDate = viewModel.startDate,
        onStartDateChange = viewModel::updateStartDate,
        endDate = viewModel.endDate,
        onEndDateChange = viewModel::updateEndDate,
        onSubmit = viewModel::createTrip,
        modifier = modifier
    )
}

@Composable
fun CreateTripScreenContent(
    uiState: CreateTripScreenUiState,
    name: String,
    onNameChange: (String) -> Unit,
    destination: String,
    onDestinationChange: (String) -> Unit,
    budget: String,
    onBudgetChange: (String) -> Unit,
    startDate: String,
    onStartDateChange: (String) -> Unit,
    endDate: String,
    onEndDateChange: (String) -> Unit,
    onSubmit: (Uri?, ContentResolver?) -> Unit,
    modifier: Modifier = Modifier
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> imageUri = uri }
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = modifier
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
    ) {

        Box(
            modifier = Modifier
                .width(120.dp)
                .height(120.dp)
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Trip image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.ic_placeholder),
                    contentDescription = "Trip Image Placeholder",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                )
            }
            IconButton(
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                onClick = {
                    launcher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Select image for the trip"
                )
            }
        }

        // Form Fields
        FormField(
            label = "Trip Name",
            placeholder = "My trip to Casablanca",
            value = name,
            onValueChange = onNameChange,
            errorMessage = if (uiState is CreateTripScreenUiState.Error) uiState.error.name else null
        )
        FormField(
            "Destination",
            "Casablanca, Morocco",
            value = destination,
            onValueChange = onDestinationChange,
            errorMessage = if (uiState is CreateTripScreenUiState.Error) uiState.error.destination else null
        )
        FormField(
            "Budget",
            "Set budget",
            value = budget,
            onValueChange = onBudgetChange,
            errorMessage = if (uiState is CreateTripScreenUiState.Error) uiState.error.budget else null,
            isNumber = true
        )

        DatePickerField(
            label = "Start Date",
            selectedDate = startDate,
            onDateSelected = onStartDateChange,
            errorMessage = if (uiState is CreateTripScreenUiState.Error) uiState.error.startDate else null,
            //modifier = Modifier.weight(1f)
        )

        DatePickerField(
            label = "End Date",
            selectedDate = endDate,
            onDateSelected = onEndDateChange,
            errorMessage = if (uiState is CreateTripScreenUiState.Error) uiState.error.endDate else null,
            //modifier = Modifier.weight(1f)
        )

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RectangularButton(
                onClick = { /* Cancel logic */ },
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }
            RectangularButton(
                onClick = {
                    onSubmit(imageUri, context.contentResolver)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Create")
            }
        }
    }
}

//@Preview
//@Composable
//fun CreateTripScreenPreview() {
//    WanderWalletTheme {
//        CreateTripScreenContent(
//            user = User(
//                id = "1",
//                username = "John Doe",
//                avatarUrl = "",
//                email = "",
//                createdAt = Date(),
//                updatedAt = Date(),
//                notifications = emptyList()
//            ),
//            currentScreen = MainContentScreen.CreateTripScreen,
//            onUpButtonClick = {},
//            onSaveClick = {},
//            navigateToProfileScreen = {},
//            navigateToTripsScreen = {},
//            notifications = emptyList(),
//            navigateToSummaryScreen = {}
//        )
//    }
//}
