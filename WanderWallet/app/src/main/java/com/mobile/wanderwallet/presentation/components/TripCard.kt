package com.mobile.wanderwallet.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mobile.wanderwallet.BuildConfig
import com.mobile.wanderwallet.R
import com.mobile.wanderwallet.data.model.Trip

@Composable
fun TripCard(
    trip: Trip,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val baseUrl = BuildConfig.BASE_URL
    Card(
        modifier = modifier
            .clickable {
                onClick(trip.id)
            }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(baseUrl + "/tripImages/" + trip.imgUrl)
                .crossfade(true)
                .build(),
            contentDescription = trip.name,
            error = painterResource(R.drawable.default_tripimage),
            placeholder = painterResource(R.drawable.default_tripimage),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().height(160.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(trip.name, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = "${trip.startDate.toString().substring(0..9)} - ${trip.endDate.toString().substring(0..9)}",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null
                    )
                    Text(
                        text = trip.destination,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            RectangularButton(
                onClick = { onClick(trip.id) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            ) {
                Text(
                    text = "View Details",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
