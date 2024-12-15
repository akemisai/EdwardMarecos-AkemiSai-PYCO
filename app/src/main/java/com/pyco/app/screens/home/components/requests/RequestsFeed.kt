package com.pyco.app.screens.home.components.requests

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pyco.app.screens.requests.components.RequestCard
import com.pyco.app.viewmodels.RequestViewModel

@Composable
fun RequestsFeed(requestViewModel: RequestViewModel) {
    // Collect the requests from the ViewModel
    val requests = requestViewModel.requests.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        if (requests.value.isEmpty()) {
            // Display message if no requests
            Text(
                text = "No Requests to Show",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            // Display requests in a LazyColumn
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(requests.value) { request ->
                    RequestCard(request)
                }
            }
        }
    }
}