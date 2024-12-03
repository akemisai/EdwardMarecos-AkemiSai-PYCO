package com.pyco.app.screens.outfits

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pyco.app.components.BottomNavigationBar
import com.pyco.app.components.backgroundColor
import com.pyco.app.components.customColor

@Composable
fun OutfitsScreen(
    navController: NavHostController
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        containerColor = backgroundColor // Sets the overall background color
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = backgroundColor // Explicitly set the background color here
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Welcome Text
                Text(
                    text = "Welcome to the Outfits Screen!",
                    color = customColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Button to Navigate to Closet
                Button(
                    onClick = { navController.navigate("closet") }
                ) {
                    Text("Go to Closet")
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_6_pro", name = "fone")
@Composable
fun OutfitsScreenPreview() {
    OutfitsScreen(
        navController = NavHostController(LocalContext.current)
    )
}
