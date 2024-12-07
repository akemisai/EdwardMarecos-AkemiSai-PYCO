package com.pyco.app.screens.outfits

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.DocumentReference
import com.pyco.app.R
import com.pyco.app.models.ClothingItem
import com.pyco.app.screens.outfits.components.ClothingItemThumbnail
import com.pyco.app.screens.outfits.components.OutfitDisplay
import com.pyco.app.screens.outfits.components.OutfitTextDetails
import com.pyco.app.viewmodels.OutfitsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitDetailScreen(
    navController: NavController,
    outfitId: String?,
    outfitsViewModel: OutfitsViewModel,
    resolveClothingItem: @Composable (DocumentReference?) -> ClothingItem?
) {
    val outfit = outfitsViewModel.outfits.collectAsState().value.find { it.id == outfitId }

    if (outfit == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Outfit not found.",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Outfit Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Divider for sectioning
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                )

                // Section: Clothing Items
                Text(
                    text = "Clothing Items",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                OutfitDisplay(
                    outfitItems = listOf(
                        "Top" to resolveClothingItem(outfit.top),
                        "Bottom" to resolveClothingItem(outfit.bottom),
                        "Shoes" to resolveClothingItem(outfit.shoe),
                        "Accessory" to resolveClothingItem(outfit.accessory)
                    ),
                    mannequinImage = R.drawable.mannequin // Replace with your mannequin image resource
                )

                // Section: Text Details
                OutfitTextDetails(
                    outfitName = outfit.name,
                    createdBy = outfit.createdBy,
                    isPublic = outfit.isPublic
                )

            }
        }
    }
}
