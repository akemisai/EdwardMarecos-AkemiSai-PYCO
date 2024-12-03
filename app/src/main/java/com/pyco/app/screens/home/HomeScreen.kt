package com.pyco.app.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pyco.app.components.BottomNavigationBar
import com.pyco.app.screens.home.components.HomeTopSection
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add

val backgroundColor = Color(0xFF333333) // Dark background color

@Composable
fun HomeScreen(
    navController: NavHostController
) {
    Scaffold(
        containerColor = backgroundColor, // Set the background color
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = Color.Transparent // Make Surface transparent to use Scaffold's background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Top Section
                HomeTopSection()

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Welcome to Your Home!",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Manage and organize your clothing items effortlessly.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:parent=pixel_6_pro", name = "HomeScreen Preview")
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        navController = NavHostController(LocalContext.current)
    )
}
