package com.pyco.app.screens.requests.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
            // Optionally add a button to view more details or respond to the request work in progress
        }
    }
}

