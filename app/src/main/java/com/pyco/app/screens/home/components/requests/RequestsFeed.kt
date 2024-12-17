package com.pyco.app.screens.home.components.requests

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import com.pyco.app.viewmodels.UserViewModel

@Composable
fun RequestsFeed(
    homeViewModel: HomeViewModel,
    userViewModel: UserViewModel,
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
                            userViewModel = userViewModel,
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
    userViewModel: UserViewModel,
    onCardClick: () -> Unit
) {

    // Observe the current user's following list
    val currentUser by userViewModel.userProfile.collectAsState()

    // Determine if the current user follows the owner of this request
    val isFollowed = currentUser?.following?.contains(request.ownerId) == true


    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xfff2f2f2),
            contentColor = backgroundColor
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
                text = request.title.ifBlank { "Error fetching request Title" },
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = backgroundColor
            )

            // Creator Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Owner",
                    tint = if (isFollowed) Color(0xFFFFD700) else if (request.ownerId == currentUser?.uid) Color(0xff89cff0) else backgroundColor, // Gold if followed and blue if u
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
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween // Space content and buttons apart
            ) {
                // top content
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f) // Allow the column to fill available space
                ) {
                    Text(
                        text = request.title.ifBlank { "Error fetching request Title" },
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

                    // request description section
                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Request Description:",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = customColor
                    )

                    Text(
                        text = request.description.ifBlank{ "Error fetching request Description" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = customColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Top Responses Section
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Top Responses",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = customColor
                    )

                    // Display responses or "Be the first to respond!" message
                    if (currentUserId == request.ownerId && request.responses.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Go touch grass, people will respond soon :)",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    } else if (request.responses.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Be the first to respond!",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    } else {
                        // Horizontal Pager for responses
                        androidx.compose.foundation.lazy.LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp) // Fixed height for the row
                        ) {
                            items(request.responses) { response ->
                                Card(
                                    shape = MaterialTheme.shapes.medium,
                                    elevation = CardDefaults.cardElevation(4.dp),
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(200.dp) // Each card takes a fixed width
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = response,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = customColor,
                                            maxLines = 2
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Buttons at the Bottom
                if (request.ownerId == currentUserId) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "U cannot respond to your own request :3",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = customColor
                        )
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
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
    }
}