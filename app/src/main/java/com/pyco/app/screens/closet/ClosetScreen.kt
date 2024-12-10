package com.pyco.app.screens.closet

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pyco.app.R
import com.pyco.app.components.BottomNavigationBar
import com.pyco.app.components.backgroundColor
import com.pyco.app.components.customColor
import com.pyco.app.navigation.Routes
import com.pyco.app.screens.closet.components.ClosetTopSection
import com.pyco.app.screens.closet.components.ClothingItemList
import com.pyco.app.viewmodels.ClosetViewModel

@Composable
fun ClosetScreen(
    navController: NavHostController,
    closetViewModel: ClosetViewModel, // Use shared ClosetViewModel
) {

    // Observe all category flows
    val tops by closetViewModel.tops.collectAsState()
    val bottoms by closetViewModel.bottoms.collectAsState()
    val shoes by closetViewModel.shoes.collectAsState()
    val accessories by closetViewModel.accessories.collectAsState()

    // Log collected data
    LaunchedEffect(tops) {
        Log.d("ClosetScreen", "Tops size: ${tops.size}")
    }
    LaunchedEffect(bottoms) {
        Log.d("ClosetScreen", "Bottoms size: ${bottoms.size}")
    }
    LaunchedEffect(shoes) {
        Log.d("ClosetScreen", "Shoes size: ${shoes.size}")
    }
    LaunchedEffect(accessories) {
        Log.d("ClosetScreen", "Accessories size: ${accessories.size}")
    }

    // Tab titles
    val tabs = listOf("Tops", "Bottoms", "Shoes", "Accessories")

    // Tab index state
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = backgroundColor,
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_wardrobe_item") },
                containerColor = Color(0xFFB0BEC5),
                contentColor = Color.White
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.upload),
                    contentDescription = "Add Fashion Item",
                    modifier = Modifier
                        .size(32.dp),
                    tint = backgroundColor
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // ClosetTopSection here
            ClosetTopSection(
                tabs = tabs,
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { index -> selectedTabIndex = index },
                userViewModel = closetViewModel.userViewModel
            )

            Spacer(modifier = Modifier.height(8.dp))

            val items = when (selectedTabIndex) {
                0 -> tops
                1 -> bottoms
                2 -> shoes
                3 -> accessories
                else -> emptyList()
            }

            // Log selected category and items
            LaunchedEffect(selectedTabIndex) {
                Log.d("ClosetScreen", "Selected Tab: ${tabs[selectedTabIndex]}")
                Log.d("ClosetScreen", "Items count: ${items.size}")
            }

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
            color = customColor
        )
    }
}
