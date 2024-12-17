package com.pyco.app.screens.reponses.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pyco.app.models.Response

@Composable
fun ResponseCard(response: Response) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Responder ID: ${response.responderId}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Comment: ${response.comment}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Timestamp: ${response.timestamp}", style = MaterialTheme.typography.bodySmall)
        }
    }
}