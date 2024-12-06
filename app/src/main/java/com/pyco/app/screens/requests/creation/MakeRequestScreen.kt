package com.pyco.app.screens.requests.creation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pyco.app.viewmodels.RequestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MakeRequestScreen(
    requestViewModel: RequestViewModel = viewModel(),
    navController: NavHostController
) {
    var color by remember { mutableStateOf("") }
    var material by remember { mutableStateOf("") }
    var wardrobeId by remember { mutableStateOf("") }  // Implement wardrobe selection as needed

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Make a Request") },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Wardrobe selection input
            TextField(
                value = wardrobeId,
                onValueChange = { wardrobeId = it },
                label = { Text("Wardrobe ID") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = color,
                onValueChange = { color = it },
                label = { Text("Color (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = material,
                onValueChange = { material = it },
                label = { Text("Material (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (wardrobeId.isNotBlank()) {
                        requestViewModel.createRequest(
                            userId = "currentUserId", // Replace with actual userId
                            wardrobeId = wardrobeId,
                            color = color,
                            material = material
                        )
                        navController.navigateUp() // Navigate back after submission
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Submit Request")
            }
        }
    }
}