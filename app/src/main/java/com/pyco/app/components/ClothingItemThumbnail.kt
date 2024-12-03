package com.pyco.app.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.res.painterResource
import com.pyco.app.R
import com.pyco.app.models.ClothingItem

@Composable
fun ClothingItemThumbnail(item: ClothingItem) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp) // Fixed width for uniform thumbnails
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = item.imageUrl,
                error = painterResource(id = R.drawable.error_placeholder),
                placeholder = painterResource(id = R.drawable.loading_placeholder)
            ),
            contentDescription = item.name,
            modifier = Modifier
                .size(64.dp)
                .padding(4.dp)
        )
        Text(
            text = item.name,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1
        )
    }
}
