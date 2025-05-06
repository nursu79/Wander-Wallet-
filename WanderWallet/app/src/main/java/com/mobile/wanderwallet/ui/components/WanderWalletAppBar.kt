package com.mobile.wanderwallet.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mobile.wanderwallet.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WanderWalletAppBar(
    title: String,
    description: String,
    descriptionStyle: TextStyle = MaterialTheme.typography.bodySmall,
    showBackIcon: Boolean = true,
    showCalendarIcon: Boolean = true,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Surface(
        color = Color(0xFF449494),
        shape = RoundedCornerShape(
            bottomStart = 16.dp,
            bottomEnd = 16.dp
        ),
        modifier = modifier
            
    ) {
        TopAppBar(title = {
            Column {
                Text(
                    text = title,
                    color = Color.Black,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = description,
                    color = Color.Black.copy(alpha = 0.8f),
                    style = descriptionStyle,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        },
            navigationIcon = {
            if (showBackIcon) {
                Surface(
                    color = Color(0xFF13E8AD),
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .size(40.dp)
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1B5E20)
                        )
                    }
                }
            }
        }, actions = {
            if (showCalendarIcon) {
                Image(
                    painter = painterResource(id = R.drawable.ic_calendar_hand),
                    contentDescription = "Calendar",
                    modifier = Modifier
                        .size(48.dp)
                        .padding(end = 12.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
            actions()
        }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        )
        )
    }
}
