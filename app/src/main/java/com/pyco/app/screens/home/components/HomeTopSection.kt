package com.pyco.app.screens.home.components

import com.pyco.app.screens.home.components.requests.RequestsFeed
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pyco.app.R
import com.pyco.app.components.backgroundColor
import com.pyco.app.components.customColor
import com.pyco.app.screens.home.components.responses.ResponsesFeed
import com.pyco.app.screens.home.components.top_outfits.TopOutfitsFeed
import com.pyco.app.viewmodels.HomeViewModel
import com.pyco.app.viewmodels.ResponseViewModel
import com.pyco.app.viewmodels.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeTopSection(
    homeViewModel: HomeViewModel,
    userViewModel: UserViewModel,
    navController: NavHostController,
    responseViewModel: ResponseViewModel

) {
    val publicOutfits by homeViewModel.publicOutfits.collectAsState()

    val userProfile by userViewModel.userProfile.collectAsState()
    val currentUserId = userProfile?.uid ?: ""

    val tabs = listOf("Requests", "Top Outfits", "Responses")
    val pagerState = rememberPagerState(
        initialPage = 1,
        pageCount = { tabs.size }
    )
    val coroutineScope = rememberCoroutineScope()

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

        // TabRow with swipe support
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = backgroundColor,
            contentColor = customColor,
            indicator = { tabPositions ->
                SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    color = customColor
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(title) },
                    selectedContentColor = customColor,
                    unselectedContentColor = customColor.copy(alpha = 0.5f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Display Content Based on Selected Tab ( now supports swipe :D )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> RequestsFeed(
                    homeViewModel = homeViewModel,
                    userViewModel = userViewModel,
                    navController = navController,
                    currentUserId = currentUserId,
                    )
                1 -> TopOutfitsFeed(
                    outfits = publicOutfits,
                    onLikeClick = { outfitId, isLiked -> homeViewModel.toggleLikeOutfit(outfitId, isLiked) },
                    fetchResolvedClothingItems = { outfit -> homeViewModel.fetchResolvedClothingItems(outfit) },
                    currentUserId = currentUserId,
                    navController = navController
                    )
                2 -> ResponsesFeed(
                    responseViewModel = responseViewModel,
                    currentUserId = currentUserId,
                    navController = navController
                )
            }
        }
    }
}