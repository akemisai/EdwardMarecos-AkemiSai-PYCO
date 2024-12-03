package com.pyco.app.screens.requests.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pyco.app.models.Request

@Composable
fun RequestCard(request: Request) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "User: ${request.userId}")
            Text(text = "Color: ${request.color ?: "Not specified"}")
            Text(text = "Material: ${request.material ?: "Not specified"}")
            Spacer(modifier = Modifier.height(8.dp))
            // Optionally add a button to view more details or respond to the request
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RequestCardPreview() {
    RequestCard(request = Request(userId = "user123", color = "Blue", material = "Cotton"))
}
