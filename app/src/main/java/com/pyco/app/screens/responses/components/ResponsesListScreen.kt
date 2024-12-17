package com.pyco.app.screens.responses.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pyco.app.components.backgroundColor
import com.pyco.app.components.customColor
import com.pyco.app.models.Response
import com.pyco.app.models.User
import com.pyco.app.viewmodels.ResponseViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResponsesListScreen(
    navController: NavHostController,
    requestId: String,
    title: String,
    responseViewModel: ResponseViewModel = viewModel()
) {
    val responses by responseViewModel.responses.collectAsState()
    val userDetails by responseViewModel.userDetails.collectAsState()

    LaunchedEffect(requestId) {
        responseViewModel.fetchResponsesForRequest(requestId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor,
                    titleContentColor = customColor,
                    navigationIconContentColor = customColor
                )
            )
        },
        containerColor = backgroundColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (responses.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No responses to display",
                            style = MaterialTheme.typography.bodyLarge,
                            color = customColor
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(responses) { response ->
                            ResponseCard(
                                response = response,
                                onClick = {
                                    navController.navigate("outfit_detail/${response.outfitId}")
                                },
                                userDetails = userDetails
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun ResponseCard(
    response: Response,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    userDetails: Map<String, User>
) {
    val responderName = userDetails[response.responderId]?.displayName ?: "Unknown"
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xfff2f2f2),
            contentColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = response.comment,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = backgroundColor
            )
            Text(
                text = "Responder: $responderName",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}