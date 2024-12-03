package com.pyco.app.screens.outfits

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pyco.app.components.BottomNavigationBar
import com.pyco.app.models.ClothingItem
import com.pyco.app.screens.outfits.components.OutfitRow
import com.pyco.app.viewmodels.ClosetViewModel
import com.pyco.app.viewmodels.OutfitsViewModel

@Composable
fun OutfitsScreen(
    navController: NavHostController,
    outfitsViewModel: OutfitsViewModel = viewModel(),
    closetViewModel: ClosetViewModel = viewModel()
) {
    val outfits by outfitsViewModel.outfits.collectAsState()
    val tops by closetViewModel.tops.collectAsState()
    val bottoms by closetViewModel.bottoms.collectAsState()
    val shoes by closetViewModel.shoes.collectAsState()
    val accessories by closetViewModel.accessories.collectAsState()

    // Map clothing items by ID for quick lookup
    val clothingItemsMap = remember { mutableStateMapOf<String, ClothingItem>() }

    LaunchedEffect(tops, bottoms, shoes, accessories) {
        (tops + bottoms + shoes + accessories).forEach { item ->
            clothingItemsMap[item.id] = item
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background, // Set dark background
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("createOutfit") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Outfit"
                )
            }
        }
    ) { innerPadding ->
        if (outfits.isEmpty()) {
            // Show a message if there are no outfits
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No outfits found. Create one from your closet!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items = outfits) { outfit -> // Explicitly name the parameter
                    OutfitRow(
                        outfit = outfit,
                        clothingItemsMap = clothingItemsMap,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium)
                            .padding(16.dp)
                    )
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OutfitsScreenPreview() {
    OutfitsScreen(
        navController = NavHostController(LocalContext.current)
    )
}
