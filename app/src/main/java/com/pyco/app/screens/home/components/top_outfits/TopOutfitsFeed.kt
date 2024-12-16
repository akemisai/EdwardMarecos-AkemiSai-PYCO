package com.pyco.app.screens.home.components.top_outfits
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.pyco.app.components.customColor
import com.pyco.app.models.ClothingItem
import com.pyco.app.models.Outfit


@Composable
fun TopOutfitsFeed(
    outfits: List<Outfit>,
    onLikeClick: (String, Boolean) -> Unit,
    fetchResolvedClothingItems: suspend (Outfit) -> List<ClothingItem>,
    currentUserId: String
) {
    if (outfits.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No outfits to display",
                style = MaterialTheme.typography.bodyLarge,
                color = customColor
            )
        }
    } else {
        val pagerState = rememberPagerState { outfits.size }

        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val outfit = outfits[page]
            var resolvedItems by remember { mutableStateOf<List<ClothingItem>?>(null) }

            LaunchedEffect(outfit) {
                resolvedItems = fetchResolvedClothingItems(outfit)
            }

            if (resolvedItems == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                OutfitCard(
                    outfit = outfit,
                    clothingItems = resolvedItems!!,
                    onLikeClick = { isLiked ->
                        onLikeClick(outfit.id, isLiked)
                    },
                    currentUserId = currentUserId
                )
            }
        }
    }
}