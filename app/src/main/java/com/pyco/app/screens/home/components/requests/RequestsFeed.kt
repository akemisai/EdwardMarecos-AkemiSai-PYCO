package com.pyco.app.screens.home.components.requests

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.pyco.app.components.backgroundColor
import com.pyco.app.components.customColor
import com.pyco.app.models.Request
import com.pyco.app.navigation.Routes
import com.pyco.app.viewmodels.HomeViewModel

@Composable
fun RequestsFeed(
    homeViewModel: HomeViewModel,
    navController: NavHostController,
    currentUserId: String,
    ) {
    val requests by homeViewModel.globalRequests.collectAsState()
    val isLoading by homeViewModel.isLoadingRequests.collectAsState()
    val errorMessage by homeViewModel.requestError.collectAsState()

    var selectedRequest by remember { mutableStateOf<Request?>(null) }

    LaunchedEffect(Unit) {
        homeViewModel.fetchGlobalRequests()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Latest Requests",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = customColor,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when {
            isLoading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = customColor)
            }

            !errorMessage.isNullOrEmpty() -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            requests.isEmpty() -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No requests to display",
                    style = MaterialTheme.typography.bodyLarge,
                    color = customColor
                )
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(requests) { request ->
                        RequestCard(
                            request = request,
                            onCardClick = { selectedRequest = request }
                        )
                    }
                }
            }
        }
    }

    // Show Dialog when a card is clicked
    selectedRequest?.let { request ->
        RequestDetailDialog(
            request = request,
            onDismiss = { selectedRequest = null },
            navController = navController,
            currentUserId = currentUserId,
            )
    }
}

@Composable
fun RequestCard(
    request: Request,
    onCardClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onCardClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Request Title
            Text(
                text = request.description,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = customColor
            )

            // Creator Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Owner",
                    tint = customColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Created by: ${request.ownerName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun RequestDetailDialog(
    request: Request,
    onDismiss: () -> Unit,
    navController: NavHostController,
    currentUserId: String,
    ) {
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false) // no more skinny dialogue
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = backgroundColor,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxSize(0.8f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = request.description,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = customColor
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .then(
                        if (request.ownerId != currentUserId) {
                            Modifier.clickable {
                                // Navigate only if it's not the current user's profile
                                navController.navigate("user_profile/${request.ownerId}")
                            }
                        } else {
                            Modifier // Non-clickable for the current user cause why would u wanna see ur public profile like this?
                        }
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Owner",
                        tint = customColor,
                        modifier = Modifier
                            .size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Created by: ${request.ownerName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = customColor
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Timestamp",
                        tint = customColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Requested at: ${request.timestamp.toDate()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Respond Button
                Button(
                    onClick = {
                        navController.navigate("${Routes.CREATE_RESPONSE}?requestId=${request.id}&ownerId=${request.ownerId}")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = customColor,
                        contentColor = backgroundColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Respond to this Request")
                }

                // Close Button
                Button(
                    onClick = { onDismiss() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close")
                }
            }
        }
    }
}