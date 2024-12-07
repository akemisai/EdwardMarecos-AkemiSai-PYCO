package com.pyco.app.screens.outfits.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pyco.app.models.ClothingItem
import coil.compose.rememberAsyncImagePainter
import com.pyco.app.R

@Composable
fun ClothingItemThumbnail(
    item: ClothingItem,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .size(80.dp)
            .padding(4.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = item.imageUrl,
                placeholder = painterResource(id = R.drawable.loading_placeholder),
                error = painterResource(id = R.drawable.error_placeholder)
            ),
            contentDescription = item.name,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.name,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1
        )
    }
}

