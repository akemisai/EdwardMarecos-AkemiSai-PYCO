package com.pyco.app.screens.outfits.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pyco.app.R
import com.pyco.app.models.ClothingItem

@Composable
fun MannequinDisplay(
    top: ClothingItem?,
    bottom: ClothingItem?,
    shoes: ClothingItem?,
    accessory: ClothingItem?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.5f) // Maintain proportions of the mannequin
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        // Mannequin background
        Image(
            painter = painterResource(id = R.drawable.mannequin),
            contentDescription = "Mannequin Silhouette",
            modifier = Modifier.fillMaxSize()
        )

        // Clothing Items
        // Top (Shirt/Torso)
        top?.let {
            ClothingItemThumbnail(
                item = it,
                modifier = Modifier
                    .size(100.dp) // Size to fit torso
                    .align(Alignment.TopCenter)
                    .offset(y = 50.dp) // Position on chest
            )
        }

        // Bottom (Pants/Legs)
        bottom?.let {
            ClothingItemThumbnail(
                item = it,
                modifier = Modifier
                    .size(120.dp) // Size to fit legs
                    .align(Alignment.Center)
                    .offset(y = 140.dp) // Position over legs
            )
        }

        // Shoes (Feet)
        shoes?.let {
            ClothingItemThumbnail(
                item = it,
                modifier = Modifier
                    .size(80.dp) // Size for shoes
                    .align(Alignment.BottomCenter)
                    .offset(y = -20.dp) // Position at feet
            )
        }

        // Accessory (Hat/Necklace)
        accessory?.let {
            ClothingItemThumbnail(
                item = it,
                modifier = Modifier
                    .size(60.dp) // Size for accessory
                    .align(Alignment.TopCenter)
                    .offset(y = -30.dp) // Position above head
            )
        }
    }
}


