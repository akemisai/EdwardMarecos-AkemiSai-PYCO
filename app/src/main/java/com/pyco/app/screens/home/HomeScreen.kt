package com.pyco.app.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pyco.app.components.BottomNavigationBar
import com.pyco.app.components.HomeTopSection
import com.pyco.app.components.backgroundColor
import com.pyco.app.components.customColor

@Composable
fun HomeScreen(
    navController: NavHostController
) {

    val statusBarPadding: Dp = with(LocalDensity.current) { 24.dp } //lower the top content so it doesn't mix with the time and battery etc.

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .padding(top = statusBarPadding) // Add top padding to move content below the status bar.
            ) {
                HomeTopSection() // Top bar using the HomeTopSection component
            }        },
        bottomBar = {
            BottomNavigationBar(navController = navController) // Bottom bar using BottomNavigationBar
        },
        contentColor = customColor,
        containerColor = backgroundColor, // background color
    ) { paddingValues ->
        // Main content here with padding from Scaffold
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Use the padding provided by the Scaffold
                .padding(16.dp) // Additional padding for internal content spacing
        ) {
            // Placeholder for the main content below the tabs
            Text(
                text = "Content goes here...",
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_6_pro", name = "fone")
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        navController = NavHostController(LocalContext.current)
    )
}