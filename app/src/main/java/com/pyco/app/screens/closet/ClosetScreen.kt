package com.pyco.app.screens.closet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.pyco.app.components.BottomNavigationBar
import com.pyco.app.components.backgroundColor
import com.pyco.app.components.customColor
import com.pyco.app.viewmodels.ClosetViewModel

@Composable
fun ClosetScreen(
    navController: NavHostController,
    closetViewModel: ClosetViewModel = viewModel()
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val wardrobeItems by closetViewModel.wardrobe.observeAsState(emptyList())
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        userId?.let {
            closetViewModel.fetchWardrobe(it) { exception ->
                showError = true
                errorMessage = exception.message ?: "Error fetching wardrobe"
            }
        }
    }

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
            color = backgroundColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                // Header
                Text(
                    text = "Your Wardrobe",
                    color = customColor,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Fetch wardrobe on first render
                LaunchedEffect(userId) {
                    userId?.let {
                        closetViewModel.fetchWardrobe(it)
                    }
                }

                // Display wardrobe items or fallback messages
                if (wardrobeItems.isNotEmpty()) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(wardrobeItems) { item ->
                            Text(
                                text = "${item.type}: ${item.color}",
                                color = customColor,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Your wardrobe is empty. Add some items!",
                        color = customColor,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Button to navigate to the upload screen
                Button(
                    onClick = { navController.navigate("upload") },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Add New Clothing Item")
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_6_pro", name = "fone")
@Composable
fun ClosetScreenPreview() {
    ClosetScreen(
        navController = NavHostController(LocalContext.current)
    )
}