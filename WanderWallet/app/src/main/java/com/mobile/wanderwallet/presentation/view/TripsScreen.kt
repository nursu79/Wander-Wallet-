package com.mobile.wanderwallet.presentation.view

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TripsScreen(
    onTripClick: (String) -> Unit,
    onLoggedOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text("Trips")
}
