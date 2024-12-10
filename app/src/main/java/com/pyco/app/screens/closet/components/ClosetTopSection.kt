// ClosetTopSection.kt
package com.pyco.app.screens.closet.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.pyco.app.R
import com.pyco.app.components.backgroundColor
import com.pyco.app.components.customColor
import com.pyco.app.viewmodels.UserViewModel
import android.util.Log
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset

@Composable
fun ClosetTopSection(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    userViewModel: UserViewModel
) {
    val userProfile by userViewModel.userProfile.collectAsState()

    // Log user profile
    LaunchedEffect(userProfile) {
        Log.d("ClosetTopSection", "UserProfile: $userProfile")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Profile picture
            userProfile?.photoURL?.let { imageUrl ->
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                )
            } ?: run {
                // Default profile picture
                Image(
                    painter = painterResource(id = R.drawable.default_profile),
                    contentDescription = "Default Profile Picture",
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column (
                modifier = Modifier.fillMaxWidth(), // Tabs take remaining space
                verticalArrangement = Arrangement.Center // Position tabs in line with profile picture
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Scrollable Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = backgroundColor,
                    contentColor = customColor,
                    edgePadding = 8.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            height = 2.dp,
                            color = customColor
                        )
                    },
                    modifier = Modifier
                        .padding(start = 16.dp) // Slightly move tabs to the left
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { onTabSelected(index) },
                            text = { Text(title) },
                            selectedContentColor = customColor,
                            unselectedContentColor = customColor.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}