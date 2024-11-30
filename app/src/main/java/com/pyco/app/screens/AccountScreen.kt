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
fun AccountScreen(
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
        Text("Account Page")

        Spacer(modifier = Modifier.height(16.dp))

        // Display user info using the exposed property
        val email = authViewModel.userEmail
        if (email != null) {
            Text("Logged in as: $email")
        } else {
            Text("User not logged in.")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigate to Settings
        Button(onClick = { navController.navigate("settings") }) {
            Text("Go to Settings")
        }
    }
}