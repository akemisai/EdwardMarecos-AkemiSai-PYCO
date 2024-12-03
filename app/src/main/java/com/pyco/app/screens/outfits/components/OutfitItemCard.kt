package com.pyco.app.screens.outfits.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.DocumentReference
import com.pyco.app.models.ClothingItem
import com.pyco.app.models.Outfit

@Composable
fun OutfitItemCard(
    outfit: Outfit,
    resolveClothingItem: (DocumentReference?) -> ClothingItem?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = outfit.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Resolve and display each clothing item
                resolveClothingItem(outfit.top)?.let { top ->
                    ClothingItemThumbnail(item = top)
                }
                resolveClothingItem(outfit.bottom)?.let { bottom ->
                    ClothingItemThumbnail(item = bottom)
                }
                resolveClothingItem(outfit.shoe)?.let { shoe ->
                    ClothingItemThumbnail(item = shoe)
                }
                resolveClothingItem(outfit.accessory)?.let { accessory ->
                    ClothingItemThumbnail(item = accessory)
                }
            }
        }
    }
}
