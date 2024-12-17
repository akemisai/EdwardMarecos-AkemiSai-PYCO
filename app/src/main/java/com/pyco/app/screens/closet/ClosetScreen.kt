package com.pyco.app.screens.closet

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import com.pyco.app.screens.closet.components.ClosetTopSection
import com.pyco.app.screens.closet.components.ClothingItemList
import com.pyco.app.viewmodels.ClosetViewModel
import kotlinx.coroutines.launch

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

    val tabs = listOf("Tops", "Bottoms", "Shoes", "Accessories")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    // Log collected data // debugging purposes
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

    Scaffold(
        containerColor = backgroundColor,
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_wardrobe_item") },
                containerColor = customColor,
                contentColor = backgroundColor
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add Fashion Item",
                    modifier = Modifier.size(32.dp),
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
            // Top Section with Scrollable Tabs
            ClosetTopSection(
                tabs = tabs,
                pagerState = pagerState,
                onTabSelected = { index ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                userViewModel = closetViewModel.userViewModel
            )

            Spacer(modifier = Modifier.height(8.dp))

            // HorizontalPager to allow swiping between categories
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val items = when (page) {
                    0 -> tops
                    1 -> bottoms
                    2 -> shoes
                    3 -> accessories
                    else -> emptyList()
                }

                if (items.isEmpty()) {
                    NoItemsMessage(message = "No ${tabs[page]} to Show")
                } else {
                    ClothingItemList(items = items)
                }
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
