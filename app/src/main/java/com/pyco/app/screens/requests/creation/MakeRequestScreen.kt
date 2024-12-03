package com.pyco.app.screens.requests.creation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pyco.app.viewmodels.RequestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MakeRequestScreen(requestViewModel: RequestViewModel = viewModel()) {
    var color by remember { mutableStateOf("") }
    var material by remember { mutableStateOf("") }
    var wardrobeId by remember { mutableStateOf("") }  // You can implement wardrobe selection here

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
            // Wardrobe selection input (this can be more complex with a list of wardrobes)
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
                    // Call ViewModel to create request
                    if (wardrobeId.isNotBlank()) {
                        requestViewModel.createRequest(
                            userId = "currentUserId", // Replace with actual userId
                            wardrobeId = wardrobeId,
                            color = color,
                            material = material
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Submit Request")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MakeRequestScreenPreview() {
    MakeRequestScreen(requestViewModel = viewModel())
}
