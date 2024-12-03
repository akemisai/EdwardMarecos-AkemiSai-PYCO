package com.pyco.app.screens.outfits.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.DocumentReference
import com.pyco.app.models.ClothingItem
import com.pyco.app.models.Outfit

@Composable
fun OutfitRow(
    outfit: Outfit,
    resolveClothingItem: (DocumentReference?) -> ClothingItem?,
    onClick: (Outfit) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(outfit) }
            .padding(16.dp)
    ) {
        Text(
            text = outfit.name,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Row for clothing item thumbnails
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Resolve and display clothing items
            listOf(
                resolveClothingItem(outfit.top),
                resolveClothingItem(outfit.bottom),
                resolveClothingItem(outfit.shoe),
                resolveClothingItem(outfit.accessory)
            ).forEach { clothingItem ->
                clothingItem?.let {
                    ClothingItemThumbnail(item = it)
                }
            }
        }
    }
}

