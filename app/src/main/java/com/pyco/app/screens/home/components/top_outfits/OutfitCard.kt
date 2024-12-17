package com.pyco.app.screens.home.components.top_outfits

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.pyco.app.R
import com.pyco.app.components.backgroundColor
import com.pyco.app.components.customColor
import com.pyco.app.models.ClothingItem
import com.pyco.app.models.ClothingType
import com.pyco.app.models.Outfit

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OutfitCard(
    outfit: Outfit,
    clothingItems: List<ClothingItem>,
    onLikeClick: (Boolean) -> Unit,
    currentUserId: String,
    navController: NavHostController
) {
    val isLiked = outfit.likes.contains(currentUserId)
    val imageUrl = outfit.creatorPhotoUrl

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = customColor,
            contentColor = backgroundColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 1. Outfit Title & Creator
            Text(
                text = outfit.name,
                style = MaterialTheme.typography.titleLarge.copy(color = backgroundColor, fontWeight = Bold)
            )
            Text(
                text = "by ${outfit.createdBy}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Outfit Items (Top/Accessory, Bottom/Shoes)
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Top row: Shirt | Accessory
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    clothingItems.find { it.type == ClothingType.TOP }?.let { shirt ->
                        Image(
                            painter = rememberAsyncImagePainter(shirt.imageUrl),
                            contentDescription = "Shirt",
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(MaterialTheme.shapes.medium),
                            contentScale = ContentScale.Fit
                        )
                    } ?: Spacer(modifier = Modifier.weight(1f))

                    clothingItems.find { it.type == ClothingType.ACCESSORY }?.let { accessory ->
                        Image(
                            painter = rememberAsyncImagePainter(accessory.imageUrl),
                            contentDescription = "Accessory",
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(MaterialTheme.shapes.medium),
                            contentScale = ContentScale.Fit
                        )
                    } ?: Spacer(modifier = Modifier.weight(1f))
                }

                // Bottom row: Pants | Shoes
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    clothingItems.find { it.type == ClothingType.BOTTOM }?.let { pants ->
                        Image(
                            painter = rememberAsyncImagePainter(pants.imageUrl),
                            contentDescription = "Pants",
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(MaterialTheme.shapes.medium),
                            contentScale = ContentScale.Fit
                        )
                    } ?: Spacer(modifier = Modifier.weight(1f))

                    clothingItems.find { it.type == ClothingType.SHOE }?.let { shoes ->
                        Image(
                            painter = rememberAsyncImagePainter(shoes.imageUrl),
                            contentDescription = "Shoes",
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(MaterialTheme.shapes.medium),
                            contentScale = ContentScale.Fit
                        )
                    } ?: Spacer(modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Tags Section (placeholder)
            Text(
                text = "Tags:",
                style = MaterialTheme.typography.titleMedium.copy(color = backgroundColor),
            )
            if (outfit.tags.isEmpty()) {
                // Display "No tags" if the outfit has no tags
                Text(
                    text = "No tags",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            } else {
                // Display tags as chips
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    outfit.tags.forEach { tag ->
                        FilterChip(
                            selected = false, // Static display, no selection behavior
                            onClick = { /* No action needed for static chips */ },
                            label = {
                                Text(
                                    text = tag,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = backgroundColor
                                )
                            },
                            shape = MaterialTheme.shapes.small,
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = customColor,
                                labelColor = backgroundColor
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Likes, Creator's Profile, Like Button
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Likes count
                Text(
                    text = "Likes: ${outfit.likes.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                // Creator's Profile Picture
                Image(
                    painter = if (imageUrl.isNotEmpty()) {
                        rememberAsyncImagePainter(imageUrl)
                    } else {
                        painterResource(id = R.drawable.default_profile)
                    },
                    contentDescription = "Creator's Profile Picture",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable {
                            if (outfit.creatorId != currentUserId) {
                                navController.navigate("user_profile/${outfit.creatorId}")
                            }
                        },
                    contentScale = ContentScale.Crop
                )

                // Like button
                IconButton(
                    onClick = { onLikeClick(!isLiked) }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.heart),
                        contentDescription = "like outfit",
                        tint = if (isLiked) Color.Red else backgroundColor,
                        modifier = Modifier.size(32.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 5. Timestamp
            Text(
                text = "Posted on: ${outfit.timestamp.toDate()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
