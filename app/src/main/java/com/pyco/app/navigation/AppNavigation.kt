package com.pyco.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pyco.app.screens.AccountScreen
import com.pyco.app.screens.SettingsScreen
import com.pyco.app.screens.authentication.LoginScreen
import com.pyco.app.screens.authentication.SignUpScreen
import com.pyco.app.screens.home.HomeScreen
import com.pyco.app.viewmodels.AuthViewModel

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                navController = navController,
            )
        }
        composable("signup") {
            SignUpScreen(
                authViewModel = authViewModel,
                navController = navController
            )
        }
        composable("home") {
            HomeScreen(
                navController = navController
            )
        }
        composable("account") {
            AccountScreen(
                authViewModel = authViewModel,
                navController = navController
            )
        }
        composable("settings") {
            SettingsScreen(
                authViewModel = authViewModel,
                navController = navController
            )
        }
    }
}
