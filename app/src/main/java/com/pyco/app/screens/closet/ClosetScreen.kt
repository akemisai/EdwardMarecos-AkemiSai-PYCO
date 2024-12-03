package com.pyco.app.screens.closet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pyco.app.components.BottomNavigationBar

@Composable
fun ClosetScreen(
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to the Closet Screen!")

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("upload") }
        ) {
            Text("Add New Clothing Item")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("home") }
        ) {
            Text("Back to Home")
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BottomNavigationBar(navController = navController)
    }
}

@Preview(showBackground = true, device = "id:pixel_6_pro", name = "fone")
@Composable
fun ClosetScreenPreview() {
    ClosetScreen(
        navController = NavHostController(LocalContext.current)
    )
}
