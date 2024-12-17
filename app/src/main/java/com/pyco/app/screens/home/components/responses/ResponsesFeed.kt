package com.pyco.app.screens.home.components.responses

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pyco.app.components.backgroundColor
import com.pyco.app.models.Request
import com.pyco.app.models.Tags
import com.pyco.app.screens.home.components.requests.RequestsFilterSection
import com.pyco.app.screens.home.components.requests.TagsPopupDialog
import com.pyco.app.viewmodels.ResponseViewModel

@Composable
fun ResponsesFeed(
    responseViewModel: ResponseViewModel = viewModel(),
    currentUserId: String,
    navController: NavHostController
) {
    val requests by responseViewModel.requests.collectAsState()
    var selectedTags by remember { mutableStateOf<List<String>>(emptyList()) } // State for active filter
    var showTagsPopup by remember { mutableStateOf(false) }
    val availableTags =
        listOf("Following") + Tags.entries.map { it.displayName } // Use all tags from the enum

    // Logic for filtering requests based on selected tags
    val filteredRequests = requests.filter { request ->
        selectedTags.isEmpty() || selectedTags.all { tag ->
            request.tags.contains(tag)
        }
    }

    // If the Tags popup is triggered, show it
    if (showTagsPopup) {
        TagsPopupDialog(
            availableTags = availableTags,
            selectedTags = selectedTags,
            onTagSelected = { tag ->
                // Toggle the tag: if already selected, remove it; if not, add it
                selectedTags = if (selectedTags.contains(tag)) {
                    selectedTags - tag
                } else {
                    selectedTags + tag
                }
            },
            onDismiss = { showTagsPopup = false }
        )
    }

    LaunchedEffect(currentUserId) {
        responseViewModel.fetchRequestsForUser(currentUserId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // Filter section at the top
        RequestsFilterSection(
            filters = availableTags,
            selectedFilters = selectedTags,
            onFilterRemoved = { tag -> selectedTags = selectedTags - tag },
            onClearAll = { selectedTags = emptyList() },
            onShowTagsPopup = { showTagsPopup = true }
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredRequests) { request ->
                if (request.ownerId == currentUserId) {
                    RequestCard(
                        request = request,
                        onClick = {
                            navController.navigate("responses_list/${request.id}/${request.title}")
                        }
                    )
                }
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
        colors = CardDefaults.cardColors(
            containerColor = Color(0xfff2f2f2),
            contentColor = backgroundColor
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = request.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = backgroundColor
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = request.description,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${request.responses.size} responses",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = backgroundColor
                )

            }
        }
    }
}

