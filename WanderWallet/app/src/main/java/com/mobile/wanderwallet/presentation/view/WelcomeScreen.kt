package com.mobile.wanderwallet.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mobile.wanderwallet.R
import com.mobile.wanderwallet.presentation.components.AppHero
import com.mobile.wanderwallet.presentation.components.RectangularButton

@Composable
fun WelcomeScreen(
    onCreateAccountClicked: () -> Unit,
    onSignInClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = 32.dp, vertical = 24.dp)
    ) {
        AppHero(imageSize = 300.dp)
        Spacer(modifier = Modifier.height(100.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.welcome_screen_text), style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 48.dp).fillMaxWidth()
            ) {
                RectangularButton(
                    onClick = onCreateAccountClicked,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Create An Account", modifier = Modifier.padding(vertical = 4.dp), style = MaterialTheme.typography.bodyLarge)
                }
                RectangularButton(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    onClick = onSignInClicked,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sign in", modifier = Modifier.padding(vertical = 4.dp), style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}