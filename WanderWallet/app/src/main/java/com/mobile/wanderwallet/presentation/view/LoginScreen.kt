package com.mobile.wanderwallet.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobile.wanderwallet.R
import com.mobile.wanderwallet.presentation.components.FormField
import com.mobile.wanderwallet.presentation.components.FormTopBar
import com.mobile.wanderwallet.presentation.components.RectangularButton
import com.mobile.wanderwallet.presentation.viewmodel.LoginScreenUiState
import com.mobile.wanderwallet.presentation.viewmodel.LoginScreenViewModel

@Composable
fun LoginScreen(
    onUpButtonClick: () -> Unit,
    onSuccessfulLogin: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginScreenViewModel = hiltViewModel()
) {
    val loginScreenUiState = viewModel.loginScreenUiState

    if (loginScreenUiState is LoginScreenUiState.Success) {
        onSuccessfulLogin()
    }
    Scaffold(
        modifier = modifier,
        topBar = {
            FormTopBar(onUpButtonClick = onUpButtonClick)
        }
    ) { contentPadding ->
        LoginScreenContent(
            uiState = loginScreenUiState,
            email = viewModel.email,
            onEmailChange = { viewModel.updateEmail(it) },
            password = viewModel.password,
            onPasswordChange = { viewModel.updatePassword(it) },
            onSubmit = viewModel::loginUser,
            onRegisterClick = onRegisterClick,
            modifier = Modifier.padding(contentPadding)
        )
    }
}

@Composable
fun LoginScreenContent(
    uiState: LoginScreenUiState,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .padding(20.dp)
    ) {
        Text("Login", style = MaterialTheme.typography.headlineSmall)
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
                    label = "Email",
                    placeholder = "example@gmail.com",
                    value = email,
                    onValueChange = onEmailChange,
                    errorMessage = if (uiState is LoginScreenUiState.Error) uiState.error.email else null,
                )
                FormField(
                    label = "Password",
                    placeholder = "********",
                    value = password,
                    onValueChange = onPasswordChange,
                    errorMessage = if (uiState is LoginScreenUiState.Error) uiState.error.password else null,
                    isPassword = true,
                    isFinal = true
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState is LoginScreenUiState.Error && uiState.error.message != null) {
                    Text(uiState.error.message, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.error)
                }
                Text(stringResource(R.string.welcome_back), style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center)
                RectangularButton(
                    onClick = onSubmit,
                    enabled = uiState !is LoginScreenUiState.Loading,
                    contentPadding = PaddingValues(horizontal = 48.dp, vertical = 8.dp)
                ) {
                    Text("Sign In", modifier = Modifier.padding(vertical = 4.dp), style = MaterialTheme.typography.bodyLarge)
                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Don't have an account?", style = MaterialTheme.typography.labelLarge)
                    TextButton(onClick = onRegisterClick) {
                        Text("Create an account", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}
