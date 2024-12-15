package com.pyco.app.screens.requests

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pyco.app.screens.requests.components.RequestCard
import com.pyco.app.viewmodels.RequestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestScreen(requestViewModel: RequestViewModel = viewModel()) {
    // Collect the requests StateFlow
    val requests = requestViewModel.requests.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Requests") },
                actions = {
                    // Optionally add a button to navigate to the "Make Request" screen
                }
            )
        }
    ) { innerPadding ->
        // Display requests in a list
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(requests.value) { request ->
                RequestCard(request)
            }
        }
    }
}

