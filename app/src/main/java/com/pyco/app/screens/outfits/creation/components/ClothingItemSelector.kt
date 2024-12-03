package com.pyco.app.screens.outfits.creation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pyco.app.models.ClothingItem
import coil.compose.rememberAsyncImagePainter
import com.pyco.app.R
import android.util.Log

@Composable
fun ClothingItemSelector(
    items: List<ClothingItem>,
    selectedItem: ClothingItem?,
    onItemSelected: (ClothingItem) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(items) { item ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .size(100.dp)
                    .clickable {
                        onItemSelected(item)
                        Log.d("ClothingItemSelector", "Selected Item: ${item.id}")
                    }
                    .padding(4.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = item.imageUrl,
                        error = painterResource(id = R.drawable.error_placeholder),
                        placeholder = painterResource(id = R.drawable.loading_placeholder)
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
                if (selectedItem == item) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}