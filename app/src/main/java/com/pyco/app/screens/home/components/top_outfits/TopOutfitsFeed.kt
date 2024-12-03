package com.pyco.app.screens.home.components.top_outfits

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pyco.app.models.Outfit
import com.pyco.app.screens.outfits.creation.components.OutfitCard

@Composable
fun TopOutfitsFeed(outfits: List<Outfit>) {
    if (outfits.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No outfits to display",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    } else {
        LazyColumn {
            items(outfits) { outfit ->
                OutfitCard(outfit = outfit) // Ensure OutfitCard is defined or imported
            }
        }
    }
}
