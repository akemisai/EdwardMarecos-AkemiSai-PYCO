package com.pyco.app.screens.closet

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pyco.app.components.BottomNavigationBar
import com.pyco.app.screens.closet.components.ClothingItemList
import com.pyco.app.screens.closet.components.ClosetTopSection
import com.pyco.app.models.ClothingItem
import com.pyco.app.viewmodels.ClosetViewModel

val closetBackgroundColor = Color(0xFF333333) // Dark background color
val customColor = Color(0xFFF7f7f7) // Assuming white for text/icons; adjust as needed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClosetScreen(
    navController: NavHostController,
    closetViewModel: ClosetViewModel = viewModel()
) {
    Scaffold(
        containerColor = closetBackgroundColor, // Set the background color
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("addWardrobeItem") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Item", tint = Color.White)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Top Section with Title and Tabs
            ClosetTopSection()

            Spacer(modifier = Modifier.height(16.dp))

            // Tabs for Clothing Categories
            var selectedTabIndex by remember { mutableIntStateOf(0) }
            val tabs = listOf("Tops", "Bottoms", "Shoes", "Accessories")

            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = closetBackgroundColor,
                contentColor = customColor,
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = customColor.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Display Clothing Items Based on Selected Tab
            when (selectedTabIndex) {
                0 -> ClothingItemList(items = closetViewModel.tops.collectAsState().value)
                1 -> ClothingItemList(items = closetViewModel.bottoms.collectAsState().value)
                2 -> ClothingItemList(items = closetViewModel.shoes.collectAsState().value)
                3 -> ClothingItemList(items = closetViewModel.accessories.collectAsState().value)
            }

            // If no items are present in the selected category, show a message
            when (selectedTabIndex) {
                0 -> {
                    if (closetViewModel.tops.collectAsState().value.isEmpty()) {
                        NoItemsMessage(message = "No Tops to Show")
                    }
                }
                1 -> {
                    if (closetViewModel.bottoms.collectAsState().value.isEmpty()) {
                        NoItemsMessage(message = "No Bottoms to Show")
                    }
                }
                2 -> {
                    if (closetViewModel.shoes.collectAsState().value.isEmpty()) {
                        NoItemsMessage(message = "No Shoes to Show")
                    }
                }
                3 -> {
                    if (closetViewModel.accessories.collectAsState().value.isEmpty()) {
                        NoItemsMessage(message = "No Accessories to Show")
                    }
                }
            }
        }
    }
}

@Composable
fun NoItemsMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = customColor.copy(alpha = 0.7f)
        )
    }
}

@Preview(showBackground = true, device = "spec:parent=pixel_6_pro", name = "ClosetScreen Preview")
@Composable
fun ClosetScreenPreview() {
    ClosetScreen(
        navController = NavHostController(LocalContext.current)
    )
}
