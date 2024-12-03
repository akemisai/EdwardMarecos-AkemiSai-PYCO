package com.pyco.app.screens.outfits.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.DocumentReference
import com.pyco.app.models.ClothingItem
import com.pyco.app.models.Outfit

@Composable
fun OutfitRow(
    outfit: Outfit,
    resolveClothingItem: (DocumentReference?) -> ClothingItem?) {
    Column {
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
            val top = resolveClothingItem(outfit.top)
            val bottom = resolveClothingItem(outfit.bottom)
            val shoe = resolveClothingItem(outfit.shoe)
            val accessory = resolveClothingItem(outfit.accessory)

            top?.let { ClothingItemThumbnail(item = it) }
            bottom?.let { ClothingItemThumbnail(item = it) }
            shoe?.let { ClothingItemThumbnail(item = it) }
            accessory?.let { ClothingItemThumbnail(item = it) }
        }
    }
}

