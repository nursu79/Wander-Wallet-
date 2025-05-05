package com.mobile.wanderwallet.presentation.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun WanderWalletApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    Surface(
        modifier = modifier
    ) {
        NavHost(
            navController = navController,
            startDestination = "splash"
        ) {
            composable(route = "splash") {
                SplashScreen(
                    onUserFound = {
                        navController.navigate("main") {
                            popUpTo("splash") { inclusive = true }
                        }
                    },
                    onUserNotFound = {
                        navController.navigate("welcome") {
                            popUpTo("splash") { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            composable(route = "welcome") {
                WelcomeScreen(
                    onCreateAccountClicked = {
                        navController.navigate("register")
                    },
                    onSignInClicked = {
                        navController.navigate("login")
                    },
                    modifier = modifier.fillMaxSize()
                )
            }

            composable(route = "login") {
                LoginScreen(
                    onUpButtonClick = {
                        navController.navigateUp()
                    },
                    onSuccessfulLogin = {
                        navController.navigate("main") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onRegisterClick = {
                        navController.navigate("register")
                    },
                    modifier = modifier.fillMaxSize()
                )
            }

            composable(route = "register") {
                RegisterScreen(
                    onUpButtonClick = {
                        navController.navigateUp()
                    },
                    onSuccessfulRegister = {
                        navController.navigate("main") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onLoginClick = {
                        navController.navigate("login")
                    },
                    modifier = modifier.fillMaxSize()
                )
            }

            composable(route = "main") {
                MainContentScreen(
                    onLoggedOut = {
                        navController.navigate("login")
                    }
                )
            }
        }
    }
}