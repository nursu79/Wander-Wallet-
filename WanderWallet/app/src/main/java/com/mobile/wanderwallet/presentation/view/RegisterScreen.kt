package com.mobile.wanderwallet.presentation.view

import android.content.ContentResolver
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.mobile.wanderwallet.R
import com.mobile.wanderwallet.presentation.components.FormField
import com.mobile.wanderwallet.presentation.components.FormTopBar
import com.mobile.wanderwallet.presentation.components.RectangularButton
import com.mobile.wanderwallet.presentation.components.SelectImageField
import com.mobile.wanderwallet.presentation.viewmodel.RegisterScreenUiState
import com.mobile.wanderwallet.presentation.viewmodel.RegisterScreenViewModel

@Composable
fun RegisterScreen(
    onUpButtonClick: () -> Unit,
    onSuccessfulRegister: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegisterScreenViewModel = hiltViewModel()
) {
    val registerScreenUiState = viewModel.registerScreenUiState

    if (registerScreenUiState is RegisterScreenUiState.Success) {
        onSuccessfulRegister()
    }
    Scaffold(
        modifier = modifier,
        topBar = {
            FormTopBar(onUpButtonClick = onUpButtonClick)
        }
    ) { contentPadding ->
        RegisterScreenContent(
            uiState = registerScreenUiState,
            email = viewModel.email,
            onEmailChange = { viewModel.updateEmail(it) },
            password = viewModel.password,
            onPasswordChange = { viewModel.updatePassword(it) },
            username = viewModel.username,
            onUsernameChange = { viewModel.updateUsername(it) },
            onSubmit = { imageUri, contentResolver -> viewModel.registerUser(imageUri, contentResolver) },
            onLoginClick = onLoginClick,
            modifier = Modifier.padding(contentPadding)
        )
    }
}

@Composable
fun RegisterScreenContent(
    uiState: RegisterScreenUiState,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    username: String,
    onUsernameChange: (String) -> Unit,
    onSubmit: (Uri?, ContentResolver?) -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> imageUri = uri }
    )
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .padding(20.dp)
    ) {
        Text("Sign Up", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.weight(1f))
        Column(
            verticalArrangement = Arrangement.spacedBy(48.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                FormField(
                    label = "Username",
                    placeholder = "Your Username",
                    value = username,
                    onValueChange = onUsernameChange,
                    errorMessage = if (uiState is RegisterScreenUiState.Error) uiState.error.username else null,
                )
                FormField(
                    label = "Email",
                    placeholder = "example@gmail.com",
                    value = email,
                    onValueChange = onEmailChange,
                    errorMessage = if (uiState is RegisterScreenUiState.Error) uiState.error.email else null,
                )
                FormField(
                    label = "Password",
                    placeholder = "********",
                    value = password,
                    onValueChange = onPasswordChange,
                    errorMessage = if (uiState is RegisterScreenUiState.Error) uiState.error.password else null,
                    isPassword = true,
                    isFinal = true
                )

                Row {
                    SelectImageField(
                        label = "Avatar",
                        launcher = launcher
                    )
                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Selected image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(40.dp).clip(CircleShape)
                        )
                    }
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState is RegisterScreenUiState.Error && uiState.error.message != null) {
                    Text(uiState.error.message, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.error)
                }
                Text(stringResource(R.string.terms), style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center)
                RectangularButton(
                    onClick = {
                        onSubmit(imageUri, context.contentResolver)
                    },
                    enabled = uiState !is RegisterScreenUiState.Loading,
                    contentPadding = PaddingValues(horizontal = 48.dp, vertical = 8.dp)
                ) {
                    Text("Create an Account", modifier = Modifier.padding(vertical = 4.dp), style = MaterialTheme.typography.bodyLarge)
                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Don't have an account?", style = MaterialTheme.typography.labelLarge)
                    TextButton(onClick = onLoginClick) {
                        Text("Sign in", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}
