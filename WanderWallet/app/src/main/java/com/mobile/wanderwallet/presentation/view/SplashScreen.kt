package com.mobile.wanderwallet.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobile.wanderwallet.presentation.components.AppHero
import com.mobile.wanderwallet.presentation.components.Indicator
import com.mobile.wanderwallet.presentation.components.RectangularButton
import com.mobile.wanderwallet.presentation.viewmodel.SplashScreenUiState
import com.mobile.wanderwallet.presentation.viewmodel.SplashScreenViewModel
import com.mobile.wanderwallet.ui.theme.WanderWalletTheme

@Composable
fun SplashScreen(
    onUserFound: () -> Unit,
    onUserNotFound: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplashScreenViewModel = hiltViewModel()
) {
    val splashScreenUiState = viewModel.splashScreenUiState

    if (splashScreenUiState is SplashScreenUiState.Error && splashScreenUiState.loggedOut) {
        onUserNotFound()
    } else if (splashScreenUiState is SplashScreenUiState.Success) {
        onUserFound()
    } else {
        SplashScreenContent(
            uiState = splashScreenUiState,
            retryAction = viewModel::getUser,
            modifier = modifier
                .padding(horizontal = 16.dp, vertical = 40.dp)
        )
    }
}

@Composable
fun SplashScreenContent(
    uiState: SplashScreenUiState,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        AppHero(imageSize = 300.dp, modifier = Modifier.padding(bottom = 64.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (uiState) {
                SplashScreenUiState.Loading -> Indicator(size = 48.dp)
                is SplashScreenUiState.Error -> {
                    Text(uiState.error.message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                    RectangularButton(
                        onClick = retryAction,
                    ) {
                        Text(
                            "Retry",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                is SplashScreenUiState.Success -> Text("You will be redirected automatically")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenLoadingPreview() {
    WanderWalletTheme {
        SplashScreenContent(uiState = SplashScreenUiState.Loading, retryAction = {})
    }
}
