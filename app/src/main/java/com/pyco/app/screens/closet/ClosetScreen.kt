package com.pyco.app.screens.closet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pyco.app.components.BottomNavigationBar
import com.pyco.app.screens.closet.components.ClosetTopSection
import com.pyco.app.screens.closet.components.ClothingItemList
import com.pyco.app.viewmodels.ClosetViewModel
import com.pyco.app.viewmodels.ClosetViewModelFactory
import com.pyco.app.viewmodels.UserViewModel

val closetBackgroundColor = Color(0xFF333333) // Dark background color
val customColor = Color(0xFFF7f7f7) // Assuming white for text/icons; adjust as needed

@Composable
fun ClosetScreen(
    navController: NavHostController,
    userViewModel: UserViewModel,
    closetViewModel: ClosetViewModel = viewModel(
        factory = ClosetViewModelFactory(userViewModel)
    )
) {
    // Observe all category flows
    val tops by closetViewModel.tops.collectAsState()
    val bottoms by closetViewModel.bottoms.collectAsState()
    val shoes by closetViewModel.shoes.collectAsState()
    val accessories by closetViewModel.accessories.collectAsState()

    // Tab titles
    val tabs = listOf("Tops", "Bottoms", "Shoes", "Accessories")

    // Tab index state
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = closetBackgroundColor,
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
            // Top Section
            ClosetTopSection()

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Row
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

            // Get the selected category items
            val items = when (selectedTabIndex) {
                0 -> tops
                1 -> bottoms
                2 -> shoes
                3 -> accessories
                else -> emptyList()
            }

            // Display items or NoItemsMessage
            if (items.isEmpty()) {
                NoItemsMessage(message = "No ${tabs[selectedTabIndex]} to Show")
            } else {
                ClothingItemList(items = items)
            }
        }
    }
}


@Composable
fun NoItemsMessage(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
    }
}
