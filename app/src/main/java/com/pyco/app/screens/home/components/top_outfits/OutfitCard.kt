package com.pyco.app.screens.home.components.top_outfits

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.pyco.app.R
import com.pyco.app.components.backgroundColor
import com.pyco.app.components.customColor
import com.pyco.app.models.ClothingItem
import com.pyco.app.models.Outfit

@Composable
fun OutfitCard(
    outfit: Outfit,
    clothingItems: List<ClothingItem>,
    onLikeClick: (Boolean) -> Unit,
    currentUserId: String
) {
    val isLiked = outfit.likes.contains(currentUserId)


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = customColor.copy(alpha = 0.8f),
            contentColor = backgroundColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Top,
        ) {
            // creator information section
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .weight(1f)
            ) {
                val imageUrl = outfit.creatorPhotoUrl
                if (imageUrl.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUrl),
                        contentDescription = "Creator's Profile Picture",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.default_profile),
                        contentDescription = "Default Profile Picture",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "Created by: ${outfit.createdBy}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Likes: ${outfit.likes.size}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // middle section with outfit items
            Column(
                modifier = Modifier
                    .weight(5f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    Text(
                        text = outfit.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = backgroundColor,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                // Display Clothing Items
                clothingItems.forEach { item ->
                    Image(
                        painter = rememberAsyncImagePainter(item.imageUrl),
                        contentDescription = item.name,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f) // Maintain 1:1 aspect ratio
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Fit // Scale to fit the allocated space
                    )
                }
            }

            Column (
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Bottom,
            ) {
                Button(
                    onClick = { onLikeClick(!isLiked) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLiked) Color.Red else backgroundColor,
                        contentColor = if (isLiked) Color.White else customColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(text = if (isLiked) "Unlike" else "Like")
                }
                // timestamp
                Text(
                    text = "Posted on: ${outfit.timestamp.toDate()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}