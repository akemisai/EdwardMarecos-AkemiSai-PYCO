package com.pyco.app.screens.outfits

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.DocumentReference
import com.pyco.app.models.ClothingItem
import com.pyco.app.models.Outfit
import com.pyco.app.screens.outfits.components.ClothingItemThumbnail
import com.pyco.app.viewmodels.OutfitsViewModel

@Composable
fun OutfitDetailScreen(
    outfitId: String?,
    outfitsViewModel: OutfitsViewModel,
    resolveClothingItem: @Composable (DocumentReference?) -> ClothingItem?
) {
    val outfit = outfitsViewModel.outfits.collectAsState().value.find { it.id == outfitId }

    if (outfit == null) {
        Text("Outfit not found.")
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = outfit.name,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

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
