package com.pyco.app.screens.home.components.responses

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pyco.app.models.Request
import com.pyco.app.viewmodels.ResponseViewModel

@Composable
fun ResponsesFeed(
    responseViewModel: ResponseViewModel = viewModel(),
    currentUserId: String,
    navController: NavHostController // Add navController parameter
) {
    val requests by responseViewModel.requests.collectAsState()

    LaunchedEffect(currentUserId) {
        responseViewModel.fetchRequestsForUser(currentUserId)
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(requests) { request ->
            if (request.ownerId == currentUserId) {
                RequestCard(
                    request = request,
                    onClick = {
                        navController.navigate("responses_list/${request.id}")
                    }
                )
            }
        }
    }
}

@Composable
fun RequestCard(
    request: Request,
    modifier: Modifier = Modifier,
    onClick: () -> Unit // Add onClick parameter
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }, // Add clickable modifier
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = request.title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = request.description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

