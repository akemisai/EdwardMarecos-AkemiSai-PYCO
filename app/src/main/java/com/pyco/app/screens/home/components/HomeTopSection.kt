package com.pyco.app.screens.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pyco.app.R
import com.pyco.app.components.backgroundColor
import com.pyco.app.components.customColor
import com.pyco.app.screens.home.components.requests.RequestsFeed
import com.pyco.app.screens.home.components.responses.ResponsesFeed
import com.pyco.app.screens.home.components.top_outfits.TopOutfitsFeed
import com.pyco.app.viewmodels.HomeViewModel

@Composable
fun HomeTopSection(
    homeViewModel: HomeViewModel,
) {
    val publicOutfits by homeViewModel.publicOutfits.collectAsState()

    val userProfile by homeViewModel.userViewModel.userProfile.collectAsState()
    val currentUserId = userProfile?.uid ?: ""

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Header Row with App Name and Icons
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // App Name
            Text(
                text = "PYCO",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = customColor
            )

            // Chat and Notification Icons
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.mis_chat),
                    contentDescription = "Chat",
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 4.dp),
                )
                Image(
                    painter = painterResource(id = R.drawable.mis_alert),
                    contentDescription = "Notification",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tab Navigation
        var selectedTabIndex by remember { mutableIntStateOf(1) }
        val tabs = listOf("Requests", "Top Outfits", "Responses")

        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = backgroundColor,
            contentColor = customColor,
            indicator = { tabPositions ->
                SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = customColor
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) },
                    selectedContentColor = customColor,
                    unselectedContentColor = customColor.copy(alpha = 0.5f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Display Content Based on Selected Tab
        when (selectedTabIndex) {
            0 -> RequestsFeed()
            1 -> TopOutfitsFeed(
                outfits = publicOutfits,
                onLikeClick = { outfitId, isLiked -> homeViewModel.toggleLikeOutfit(outfitId, isLiked) },
                fetchResolvedClothingItems = { outfit -> homeViewModel.fetchResolvedClothingItems(outfit) },
                currentUserId = currentUserId
            )
            2 -> ResponsesFeed()
        }
    }
}