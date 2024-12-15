package com.pyco.app.screens.outfits.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.DocumentReference
import com.pyco.app.components.backgroundColor
import com.pyco.app.components.customColor
import com.pyco.app.models.ClothingItem
import com.pyco.app.models.Outfit

@Composable
fun OutfitCardGridItem(
    outfit: Outfit,
    resolveClothingItem: (DocumentReference?) -> ClothingItem?,
    onClick: (Outfit) -> Unit
) {
    Column() {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clickable{ onClick(outfit) }
                .padding(4.dp),
            colors = CardDefaults.cardColors(containerColor = customColor)
        ) {
            val clothingItems = listOf(outfit.top, outfit.bottom, outfit.shoe).mapNotNull(resolveClothingItem)
            if (clothingItems.isNotEmpty()) {
                // Display images in quadrants
                QuadrantLayout(
                    topItem = resolveClothingItem(outfit.top),
                    bottomItem = resolveClothingItem(outfit.bottom),
                    shoeItem = resolveClothingItem(outfit.shoe),
                    accessoryItem = resolveClothingItem(outfit.accessory)
                )
            } else {
                Text(
                    text = "No Images",
                    style = MaterialTheme.typography.bodySmall,
                    color = backgroundColor,
                    textAlign = TextAlign.Center
                )
            }
        }
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            // Outfit text info
            Text(
                text = outfit.name,
                style = MaterialTheme.typography.bodyMedium,
                color = customColor,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            // Timestamp
            Text(
                text = "Uploaded: ${outfit.timestamp.toDate()}",
                style = MaterialTheme.typography.bodySmall,
                color = customColor.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
fun QuadrantLayout(
    topItem: ClothingItem?,
    bottomItem: ClothingItem?,
    shoeItem: ClothingItem?,
    accessoryItem: ClothingItem?) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top Item (Q2)
        if (topItem != null) {
            Image(
                painter = rememberAsyncImagePainter(topItem.imageUrl),
                contentDescription = topItem.name,
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.TopCenter),
                contentScale = ContentScale.Fit
            )
        }

        // Bottom Item (Q1)
        if (bottomItem != null) {
            Image(
                painter = rememberAsyncImagePainter(bottomItem.imageUrl),
                contentDescription = bottomItem.name,
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.BottomCenter),
                contentScale = ContentScale.Fit
            )
        }

        // Shoe Item (Q3)
        if (shoeItem != null) {
            Image(
                painter = rememberAsyncImagePainter(shoeItem.imageUrl),
                contentDescription = shoeItem.name,
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.CenterStart),
                contentScale = ContentScale.Fit
            )
        }

        // Accessory Item (Q4)
        if (accessoryItem != null) {
            Image(
                painter = rememberAsyncImagePainter(accessoryItem.imageUrl),
                contentDescription = accessoryItem.name,
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.CenterEnd),
                contentScale = ContentScale.Fit
            )
        }
    }
}