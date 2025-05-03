package com.mobile.wanderwallet.presentation.view

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobile.wanderwallet.presentation.viewmodel.WanderWalletAppViewModel

@Composable
fun WanderWalletApp(
    viewModel: WanderWalletAppViewModel = hiltViewModel<WanderWalletAppViewModel>(),
    modifier: Modifier = Modifier
) {
    Surface(color = MaterialTheme.colorScheme.primary) {}
}