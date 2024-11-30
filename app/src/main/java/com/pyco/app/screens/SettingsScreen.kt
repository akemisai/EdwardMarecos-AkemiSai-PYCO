package com.pyco.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pyco.app.viewmodels.AuthViewModel

@Composable
fun SettingsScreen(
    authViewModel: AuthViewModel,
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Settings Page")

        Spacer(modifier = Modifier.height(16.dp))

        // Logout Button
        Button(onClick = {
            authViewModel.logOut()
            navController.navigate("login") {
                popUpTo("home") { inclusive = true } // Clear the navigation stack
            }
        }) {
            Text("Log Out")
        }
    }
}
