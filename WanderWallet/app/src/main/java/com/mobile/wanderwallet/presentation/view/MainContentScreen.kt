package com.mobile.wanderwallet.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.mobile.wanderwallet.BuildConfig
import com.mobile.wanderwallet.R
import com.mobile.wanderwallet.data.model.User
import com.mobile.wanderwallet.presentation.components.Indicator
import com.mobile.wanderwallet.presentation.components.RectangularButton
import com.mobile.wanderwallet.presentation.viewmodel.MainContentScreenUiState
import com.mobile.wanderwallet.presentation.viewmodel.MainContentScreenViewModel

sealed class MainContentScreen(val route: String, val title: String, val subString: String? = null) {
    data object TripsScreen: MainContentScreen(route = "trips", title = "Trips", subString = "Explore your trips")
    data object TripDetailsScreen: MainContentScreen(route = "trips/{id}", title = "Trip Details") {
        fun createRoute(id: String) = "trips/$id"
    }
    data object CreateTripScreen: MainContentScreen(route = "createTrip", title = "Where to next?", subString = "Start planning for the trip!")
    data object ProfileScreen: MainContentScreen(route = "profile", title = "Your profile")
    data object SummaryScreen: MainContentScreen(route = "summary", title = "Summary")

}

fun getScreenFromRoute(route: String?): MainContentScreen {
    return when {
        route == MainContentScreen.TripsScreen.route -> MainContentScreen.TripsScreen
        route == MainContentScreen.CreateTripScreen.route -> MainContentScreen.CreateTripScreen
        (route?.startsWith("trips/") ?: false) -> MainContentScreen.TripDetailsScreen
        else -> MainContentScreen.TripsScreen
    }
}

@Composable
fun MainContentScreen(
    onLoggedOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MainContentScreenViewModel = hiltViewModel(),
) {
    val uiState = viewModel.mainContentScreenUiState

    MainContentScreenContent(
        onLoggedOut = onLoggedOut,
        uiState = uiState,
        getUser = viewModel::getUser,
        modifier = modifier
    )
}

@Composable
fun MainContentScreenContent(
    onLoggedOut: () -> Unit,
    uiState: MainContentScreenUiState,
    getUser: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is MainContentScreenUiState.Error -> {
            if (uiState.loggedOut) {
                onLoggedOut()
            } else {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = modifier
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(uiState.error.message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                        RectangularButton(
                            onClick = getUser,
                        ) {
                            Text(
                                "Retry",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
        MainContentScreenUiState.Loading -> {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier
            ) {
                Indicator(size = 48.dp)
            }
        }
        is MainContentScreenUiState.Success -> {
            MainContentNavigation(
                user = uiState.data.user,
                onLoggedOut = onLoggedOut,
                modifier = modifier
            )
        }
    }
}

@Composable
fun MainContentNavigation(
    user: User,
    onLoggedOut: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = getScreenFromRoute(
        backStackEntry?.destination?.route
    )

    Scaffold(
        topBar = {
            MainContentAppBar(
                user = user,
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                modifier = Modifier
                    .height(160.dp)
            )
        },
        bottomBar = {
            MainContentBottomBar(
                currentScreen = currentScreen,
                navigateToProfileScreen = {
                    if (currentScreen !is MainContentScreen.ProfileScreen) {
                        navController.navigate(MainContentScreen.ProfileScreen.route)
                    }
                },
                navigateToTripsScreen = {
                    if (currentScreen !is MainContentScreen.TripsScreen) {
                        navController.navigate(MainContentScreen.TripsScreen.route)
                    }
                },
                navigateToSummaryScreen = {
                    if (currentScreen !is MainContentScreen.SummaryScreen) {
                        navController.navigate(MainContentScreen.SummaryScreen.route)
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MainContentScreen.TripsScreen.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = MainContentScreen.TripsScreen.route) {
                TripsScreen(
                    onTripClick = {
                        navController.navigate(MainContentScreen.TripDetailsScreen.createRoute(it))
                    },
                    onLoggedOut = onLoggedOut
                )
            }

            composable(
                route = MainContentScreen.TripDetailsScreen.route,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEnt ->
                val tripId = backStackEnt.arguments?.getString("id") ?: ""
                TripDetailsScreen(
                    id = tripId,
                    onLoggedOut = onLoggedOut
                )
            }

            composable(
                route = MainContentScreen.CreateTripScreen.route
            ) {
                CreateTripScreen(
                    onSuccess = {
                        navController.navigate(MainContentScreen.TripDetailsScreen.createRoute(it))
                    },
                    onLoggedOut = onLoggedOut
                )
            }
        }
    }
}

@Composable
fun MainContentAppBar(
    user: User,
    currentScreen: MainContentScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, top = 32.dp, end = 24.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxHeight()
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (canNavigateBack) {
                        IconButton(
                            onClick = navigateUp,
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Go back"
                            )
                        }
                    }
                    Text(
                        "Hi ${user.username.split(" ")[0]} \uD83D\uDC4B",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text(currentScreen.title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary)
                    if (currentScreen.subString != null) {
                        Text(currentScreen.subString, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxHeight()
            ) {
                Image(
                    painter = painterResource(R.drawable.topbar_image),
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )
                AsyncImage(
                    model = BuildConfig.BASE_URL + "/" + user.avatarUrl,
                    contentDescription = "User avatar",
                    placeholder = painterResource(R.drawable.default_avatar),
                    error = painterResource(R.drawable.default_avatar),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
            }
        }
    }
}

@Composable
fun MainContentBottomBar(
    currentScreen: MainContentScreen,
    navigateToProfileScreen: () -> Unit,
    navigateToTripsScreen: () -> Unit,
    navigateToSummaryScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = navigateToProfileScreen,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = if (currentScreen is MainContentScreen.ProfileScreen) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountCircle,
                    contentDescription = "Go to profile screen",
                    modifier = Modifier.size(24.dp)
                )
            }
            IconButton(
                onClick = navigateToTripsScreen,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = if (currentScreen is MainContentScreen.TripsScreen) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = "Go to home screen",
                    modifier = Modifier.size(24.dp)
                )
            }
            IconButton(
                onClick = navigateToSummaryScreen,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = if (currentScreen is MainContentScreen.SummaryScreen) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.QueryStats,
                    contentDescription = "Go to summary screen",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
