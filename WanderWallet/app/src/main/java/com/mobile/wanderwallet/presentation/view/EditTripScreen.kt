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
import coil.request.ImageRequest
import com.mobile.wanderwallet.BuildConfig
import com.mobile.wanderwallet.R
import com.mobile.wanderwallet.presentation.components.DatePickerField
import com.mobile.wanderwallet.presentation.components.FormField
import com.mobile.wanderwallet.presentation.components.RectangularButton
import com.mobile.wanderwallet.presentation.viewmodel.EditTripScreenUiState
import com.mobile.wanderwallet.presentation.viewmodel.EditTripScreenViewModel

@Composable
fun EditTripScreen(
    onSuccess: () -> Unit,
    onCancel: () -> Unit,
    onLoggedOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditTripScreenViewModel = hiltViewModel()
) {
    val uiState = viewModel.editTripScreenUiState

    if ((uiState is EditTripScreenUiState.Error) && uiState.loggedOut) {
        onLoggedOut()
    }

    if (uiState is EditTripScreenUiState.Success) {
        onSuccess()
    }

    EditTripScreenContent(
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
        tripImageUrl = viewModel.tripImageUrl,
        onSubmit = viewModel::updateTrip,
        onCancel = onCancel,
        modifier = modifier
    )
}

@Composable
fun EditTripScreenContent(
    uiState: EditTripScreenUiState,
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
    tripImageUrl: String?,
    onSubmit: (Uri?, ContentResolver?) -> Unit,
    onCancel: () -> Unit,
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
                if (tripImageUrl != null) {
                    val baseUrl = BuildConfig.BASE_URL
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("$baseUrl/tripImages/$tripImageUrl")
                            .crossfade(true)
                            .build(),
                        contentDescription = "Trip image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.default_tripimage),
                        contentDescription = "Trip Image Placeholder",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    )
                }
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
            errorMessage = if (uiState is EditTripScreenUiState.Error) uiState.error.name else null
        )
        FormField(
            "Destination",
            "Casablanca, Morocco",
            value = destination,
            onValueChange = onDestinationChange,
            errorMessage = if (uiState is EditTripScreenUiState.Error) uiState.error.destination else null
        )
        FormField(
            "Budget",
            "Set budget",
            value = budget,
            onValueChange = onBudgetChange,
            errorMessage = if (uiState is EditTripScreenUiState.Error) uiState.error.budget else null,
            isNumber = true
        )

        DatePickerField(
            label = "Start Date",
            selectedDate = startDate,
            onDateSelected = onStartDateChange,
            errorMessage = if (uiState is EditTripScreenUiState.Error) uiState.error.startDate else null,
            //modifier = Modifier.weight(1f)
        )

        DatePickerField(
            label = "End Date",
            selectedDate = endDate,
            onDateSelected = onEndDateChange,
            errorMessage = if (uiState is EditTripScreenUiState.Error) uiState.error.endDate else null,
            //modifier = Modifier.weight(1f)
        )

        if (uiState is EditTripScreenUiState.Error) {
            Text(uiState.error.message ?: "An unexpected error occurred 2", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.error)
        }
        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RectangularButton(
                onClick = onCancel,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onError)
            }
            RectangularButton(
                onClick = {
                    onSubmit(imageUri, context.contentResolver)
                },
                enabled = uiState !is EditTripScreenUiState.Loading,
                modifier = Modifier.weight(1f)
            ) {
                Text("Update", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

//@Composable
//fun EditTripScreen(
//    user: User,
//    currentScreen: MainContentScreen,
//    onUpButtonClick: () -> Unit,
//    onSaveClick: () -> Unit,
//    navigateToProfileScreen: () -> Unit,
//    navigateToTripsScreen: () -> Unit,
//    navigateToSummaryScreen: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    val imageUri by remember { mutableStateOf<Uri?>(null) }
//    var tripName by remember { mutableStateOf("Trip to Japan") }
//    var destination by remember { mutableStateOf("Tokyo") }
//    var budget by remember { mutableStateOf("3000") }
//    var startDate by remember { mutableStateOf("2024-05-10") }
//    var endDate by remember { mutableStateOf("2024-05-20") }
//
//    Scaffold(
//        topBar = {
//            MainContentAppBar(
//                user = user,
//                currentScreen = currentScreen,
//                canNavigateBack = true,
//                navigateUp = onUpButtonClick,
//                modifier = Modifier.height(160.dp)
//            )
//        },
//        bottomBar = {
//            MainContentBottomBar(
//                currentScreen = currentScreen,
//                navigateToProfileScreen = navigateToProfileScreen,
//                navigateToTripsScreen = navigateToTripsScreen,
//                navigateToSummaryScreen = navigateToSummaryScreen
//            )
//        },
//        modifier = modifier
//    ) { innerPadding ->
//        Column(
//            modifier = Modifier
//                .padding(innerPadding)
//                .padding(horizontal = 16.dp)
//                .verticalScroll(rememberScrollState())
//                .fillMaxSize()
//        ) {
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Box(
//                    contentAlignment = Alignment.Center,
//                    modifier = Modifier
//                        .size(120.dp)
//                        .clip(RoundedCornerShape(16.dp))
//                        .background(MaterialTheme.colorScheme.surfaceVariant)
//                ) {
//                    if (imageUri != null) {
//                        AsyncImage(
//                            model = imageUri,
//                            contentDescription = "Trip image",
//                            contentScale = ContentScale.Crop,
//                            modifier = Modifier.fillMaxSize()
//                        )
//
//                    }
//                    else {
//                        Icon(
//                            painter = painterResource(id = R.drawable.ic_placeholder),
//                            contentDescription = "Trip Image Placeholder",
//                            modifier = Modifier.size(48.dp)
//                        )
//                    }
//                }
//                Spacer(modifier = Modifier.height(24.dp))
//                Text("Trip Image (Editing)", style = MaterialTheme.typography.labelMedium)
//            }
//
//
//
//            FormField(
//                "Trip Name",
//                "Edit trip name",
//                tripName,
//                { tripName = it }
//
//                )
//            FormField(
//                "Destination",
//                "Edit destination",
//                destination,
//                { destination = it }
//
//                )
//            FormField(
//                "Budget",
//                "Edit budget",
//                budget,
//                { budget = it },
//                errorMessage = null,
//                isNumber = true,
//
//                )
//            Spacer(modifier = Modifier.height(24.dp))
//
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                Box(modifier = Modifier.weight(1f)) {
//                    DatePickerField("Start Date", startDate, { startDate = it })
//                }
//                Box(modifier = Modifier.weight(1f)) {
//                    DatePickerField("End Date", endDate, { endDate = it })
//                }
//            }
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                RectangularButton(
//                    onClick = { /* Cancel logic */ },
//                    color = MaterialTheme.colorScheme.surface,
//                    contentColor = MaterialTheme.colorScheme.onSurface,
//                    modifier = Modifier.weight(1f)
//                ) {
//                    Text("Cancel")
//                }
//                RectangularButton(
//                    onClick = onSaveClick,
//                    modifier = Modifier.weight(1f)
//                ) {
//                    Text("Save Changes")
//                }
//            }
//
//            Spacer(modifier = Modifier.height(32.dp))
//        }
//    }
//}
//
//@Preview
//@Composable
//fun EditTripScreenPreview() {
//    WanderWalletTheme {
//        EditTripScreen(
//            user = User(
//                id = "2",
//                username = "Jane Smith",
//                avatarUrl = "",
//                email = "",
//                createdAt = Date(),
//                updatedAt = Date(),
//                notifications = emptyList()
//            ),
//            currentScreen = MainContentScreen.EditTripScreen,
//            onUpButtonClick = {},
//            onSaveClick = {},
//            navigateToProfileScreen = {},
//            navigateToTripsScreen = {},
//            navigateToSummaryScreen = {}
//        )
//    }
//}
