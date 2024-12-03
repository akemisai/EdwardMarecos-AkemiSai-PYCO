package com.pyco.app.screens.outfits.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pyco.app.models.ClothingItem
import com.pyco.app.models.Outfit

@Composable
fun OutfitRow(
    outfit: Outfit,
    clothingItemsMap: Map<String, ClothingItem>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = outfit.name,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Display each item in the outfit
            val top = clothingItemsMap[outfit.topId]
            val bottom = clothingItemsMap[outfit.bottomId]
            val shoe = clothingItemsMap[outfit.shoeId]
            val accessory = clothingItemsMap[outfit.accessoryId]

            if (top != null) {
                ClothingItemThumbnail(item = top)
            }
            if (bottom != null) {
                ClothingItemThumbnail(item = bottom)
            }
            if (shoe != null) {
                ClothingItemThumbnail(item = shoe)
            }
            if (accessory != null) {
                ClothingItemThumbnail(item = accessory)
            }
        }
    }
}
