package com.pyco.app.screens.outfits.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pyco.app.models.ClothingItem

@Composable
fun OutfitDisplay(
    outfitItems: List<Pair<String, ClothingItem?>>,
    mannequinImage: Int // Resource ID of the mannequin image
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.5f) // Adjust ratio for mannequin proportions
    ) {
        // Mannequin background
        Image(
            painter = painterResource(mannequinImage),
            contentDescription = "Mannequin",
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
        )

        outfitItems.forEach { (label, item) ->
            when (label) {
                "Top" -> item?.let {
                    ClothingItemThumbnail(
                        item = it,
                        modifier = Modifier
                            .size(100.dp) // Adjust size as needed
                            .align(Alignment.TopCenter)
                            .offset(y = 80.dp) // Position over the chest
                    )
                }
                "Bottom" -> item?.let {
                    ClothingItemThumbnail(
                        item = it,
                        modifier = Modifier
                            .size(120.dp) // Adjust size as needed
                            .align(Alignment.Center)
                            .offset(y = 100.dp) // Position over the legs
                    )
                }
                "Shoes" -> item?.let {
                    ClothingItemThumbnail(
                        item = it,
                        modifier = Modifier
                            .size(80.dp) // Adjust size as needed
                            .align(Alignment.BottomCenter)
                            .offset(y = -50.dp) // Position at the feet
                    )
                }
                "Accessory" -> item?.let {
                    ClothingItemThumbnail(
                        item = it,
                        modifier = Modifier
                            .size(60.dp) // Adjust size as needed
                            .align(Alignment.TopCenter)
                            .offset(y = -50.dp) // Position above the mannequin's head
                    )
                }
            }
        }
    }
}


