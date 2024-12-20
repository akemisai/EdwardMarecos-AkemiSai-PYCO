// ClothingItemRow.kt
package com.pyco.app.screens.closet.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.pyco.app.R
import com.pyco.app.models.ClothingItem

@Composable
fun ClothingItemRow(item: ClothingItem) {
    // Log each item being displayed
    LaunchedEffect(item) {
        Log.d("ClothingItemRow", "Displaying item: ${item.name}")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Image with placeholders
        Image(
            painter = rememberAsyncImagePainter(
                model = item.imageUrl,
                error = painterResource(id = R.drawable.error_placeholder),
                placeholder = painterResource(id = R.drawable.loading_placeholder)
            ),
            contentDescription = item.name,
            modifier = Modifier
                .size(64.dp)
                .padding(end = 16.dp)
        )
        // Details
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White // Ensure text is visible against dark background
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Color: ${item.colour}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray
            )
            Text(
                text = "Material: ${item.material}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray
            )
        }
    }
}