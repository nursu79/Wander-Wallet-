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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mobile.wanderwallet.BuildConfig
import com.mobile.wanderwallet.R
import com.mobile.wanderwallet.presentation.components.FormField
import com.mobile.wanderwallet.presentation.components.Indicator
import com.mobile.wanderwallet.presentation.components.RectangularButton
import com.mobile.wanderwallet.presentation.viewmodel.ProfileScreenUiState
import com.mobile.wanderwallet.presentation.viewmodel.ProfileScreenViewModel

@Composable
fun ProfileScreen(
    onUpdateSuccess: () -> Unit,
    onLoggedOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileScreenViewModel = hiltViewModel()
) {
    val uiState = viewModel.profileScreenUiState

    if (uiState is ProfileScreenUiState.Error && uiState.loggedOut) {
        onLoggedOut()
    }
    if (uiState is ProfileScreenUiState.Success) {
        onUpdateSuccess()
    }
    when (uiState) {
        ProfileScreenUiState.GetLoading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Indicator(size = 48.dp)
            }
        }
        else -> {
            ProfileScreenContent(
                uiState = uiState,
                username = viewModel.username,
                onUsernameChange = viewModel::updateUsername,
                email = viewModel.email,
                onEmailChange = viewModel::updateEmail,
                newPassword = viewModel.newPassword,
                onNewPasswordChange = viewModel::updateNewPassword,
                oldPassword = viewModel.oldPassword,
                onOldPasswordChange = viewModel::updateOldPassword,
                onSubmit = viewModel::updateProfile,
                onLogout = {
                    viewModel.logoutUser(onLoggedOut)
                },
                avatarUrl = viewModel.avatarUrl,
                modifier = modifier
            )
        }
    }
}

@Composable
fun ProfileScreenContent(
    uiState: ProfileScreenUiState,
    username: String,
    onUsernameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    newPassword: String,
    onNewPasswordChange: (String) -> Unit,
    oldPassword: String,
    onOldPasswordChange: (String) -> Unit,
    avatarUrl: String?,
    onSubmit: (Uri?, ContentResolver?) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> imageUri = uri }
    )

    var showPopup by rememberSaveable { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center
    ) {
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
                    if (avatarUrl != null) {
                        val baseUrl = BuildConfig.BASE_URL
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("$baseUrl/userAvatars/$avatarUrl")
                                .crossfade(true)
                                .build(),
                            contentDescription = "User Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.default_avatar),
                            contentDescription = "Avatar Placeholder",
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
                        contentDescription = "Select an avatar image"
                    )
                }
            }

            // Form Fields
            FormField(
                label = "Username",
                placeholder = "Username",
                value = username,
                onValueChange = onUsernameChange,
                errorMessage = if (uiState is ProfileScreenUiState.Error) uiState.error.username else null
            )
            FormField(
                label = "Email",
                placeholder = "example@gmail.com",
                value = email,
                onValueChange = onEmailChange,
                errorMessage = if (uiState is ProfileScreenUiState.Error) uiState.error.email else null
            )
            FormField(
                "New Password",
                "New password",
                value = newPassword,
                onValueChange = onNewPasswordChange,
                errorMessage = if (uiState is ProfileScreenUiState.Error) uiState.error.password else null,
                isPassword = true,
                isFinal = true
            )

            if (uiState is ProfileScreenUiState.Error) {
                Text(
                    uiState.error.message ?: "An unexpected error occurred 2",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    onClick = onLogout
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            "Logout",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                RectangularButton(
                    onClick = { showPopup = true },
                    enabled = uiState !is ProfileScreenUiState.UpdateLoading,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Update", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        if (showPopup) {
            Popup(
                onDismissRequest = { showPopup = false },
                properties = PopupProperties(focusable = true)
            ) {
                Card(
                    elevation = CardDefaults.cardElevation(8.dp),
                    modifier = Modifier
                        .padding(20.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Verify")
                        FormField(
                            label = "Your password",
                            placeholder = "********",
                            value = oldPassword,
                            onValueChange = onOldPasswordChange,
                            isPassword = true
                        )
                        RectangularButton(
                            onClick = {
                                showPopup = false
                                onSubmit(imageUri, context.contentResolver)
                            },
                            enabled = uiState !is ProfileScreenUiState.UpdateLoading,
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(0.9f)
                        ) {
                            Text("Update", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

//@Composable
//fun ProfileScreen(
//    user: User,
//    currentScreen: MainContentScreen,
//    onLogoutClick: () -> Unit,
//    navigateToTripsScreen: () -> Unit,
//    navigateToSummaryScreen: () -> Unit,
//    navigateToProfileScreen: () -> Unit
//) {
//    var name by remember { mutableStateOf(user.username) }
//    var email by remember { mutableStateOf(user.email) }
//    var password by remember { mutableStateOf("") }
//
//    Scaffold(
//        topBar = {
//            MainContentAppBar(
//                user = user,
//                currentScreen = currentScreen,
//                canNavigateBack = false,
//                navigateUp = {},
//                modifier = Modifier.height(160.dp)
//            )
//        },
//        bottomBar = {
//            MainContentBottomBar(
//                currentScreen = currentScreen,
//                navigateToTripsScreen = navigateToTripsScreen,
//                navigateToSummaryScreen = navigateToSummaryScreen,
//                navigateToProfileScreen = navigateToProfileScreen
//            )
//        }
//    ) { innerPadding ->
//        Column(
//            modifier = Modifier
//                .padding(innerPadding)
//                .padding(horizontal = 16.dp)
//                .verticalScroll(rememberScrollState())
//                .fillMaxSize()
//        ) {
//            Spacer(modifier = Modifier.height(24.dp))
//
//            // Avatar
//            Column(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Image(
//                    painter = rememberAsyncImagePainter(model = user.avatarUrl),
//                    contentDescription = "Profile Picture",
//                    modifier = Modifier
//                        .size(120.dp)
//                        .clip(MaterialTheme.shapes.medium)
//                        .background(MaterialTheme.colorScheme.surfaceVariant),
//                    contentScale = ContentScale.Crop
//                )
//                Spacer(modifier = Modifier.height(16.dp))
//                Text("Edit Profile", style = MaterialTheme.typography.titleSmall)
//            }
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            // Form fields
//            FormField(
//                label = "Name",
//                placeholder = "Your name",
//                value = name,
//                onValueChange = { name = it },
//                errorMessage = null
//            )
//            Spacer(modifier = Modifier.height(24.dp))
//            FormField(
//                label = "Email",
//                placeholder = "Your email",
//                value = email,
//                onValueChange = { email = it },
//                errorMessage = null
//            )
//            Spacer(modifier = Modifier.height(24.dp))
//            FormField(
//                label = "Password",
//                placeholder = " Update Password",
//                value = password,
//                onValueChange = { password = it },
//                errorMessage = null,
//                isPassword = true,
//                isFinal = true
//            )
//
//            Spacer(modifier = Modifier.height(48.dp))
//
//            // Logout
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.Center
//            ) {
//                TextButton(onClick = onLogoutClick) {
//                    Icon(Icons.Default.Logout, contentDescription = "Logout")
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text("Log out", style = MaterialTheme.typography.labelLarge)
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
//fun ProfileScreenPreview() {
//    WanderWalletTheme {
//        ProfileScreen(
//            user = User(
//                id = "1",
//                username = "Alice Johnson",
//                avatarUrl = "https://randomuser.me/api/portraits/women/1.jpg",
//                email = "alice@example.com",
//                createdAt = Date(),
//                updatedAt = Date(),
//                notifications = emptyList()
//
//            ),
//            currentScreen = MainContentScreen.ProfileScreen,
//            onLogoutClick = {},
//            navigateToTripsScreen = {},
//            navigateToSummaryScreen = {},
//            navigateToProfileScreen = {}
//        )
//    }
//}
