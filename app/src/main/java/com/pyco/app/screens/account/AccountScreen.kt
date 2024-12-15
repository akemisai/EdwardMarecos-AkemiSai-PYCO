package com.pyco.app.screens.account

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.pyco.app.R
import com.pyco.app.components.BottomNavigationBar
import com.pyco.app.components.backgroundColor
import com.pyco.app.components.customColor
import com.pyco.app.models.Outfit
import com.pyco.app.models.User
import com.pyco.app.viewmodels.UserViewModel
import kotlin.contracts.contract

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    userViewModel: UserViewModel = viewModel(),
    navController: NavHostController
) {
    val userProfile by userViewModel.userProfile.collectAsState()
    val userPublicOutfits by userViewModel.userPublicOutfits.collectAsState()

    val isLoading by userViewModel.isLoading.collectAsState()

    Scaffold(
        containerColor = backgroundColor,
        bottomBar = { BottomNavigationBar(navController = navController) },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor,
                    titleContentColor = customColor,
                ),
                title = {
                    Text(
                        text = "PYCO",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = customColor
                    )
                },
                actions = {
                    IconButton(onClick = { /* TODO: Navigate to notifications */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = customColor
                        )
                    }
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = customColor
                        )
                    }
                },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Profile picture
            userProfile?.photoURL?.let { imageUrl ->
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )
            } ?: run {
                // Default profile picture
                Image(
                    painter = painterResource(id = R.drawable.default_profile),
                    contentDescription = "Default Profile Picture",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // User name
            Text(
                text = userProfile?.displayName ?: "Jane Doe",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = customColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Followers, Following, and Likes stats
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 40.dp, end = 40.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                userProfile?.let { profile ->
                    EngagementStat(count = profile.followersCount, label = "followers", iconColor = customColor)
                    EngagementStat(count = profile.followingCount, label = "following", iconColor = customColor)
                    EngagementStat(count = profile.likesCount, label = "likes", iconColor = Color(0xff852221))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Edit profile button
            Button(
                onClick = { navController.navigate("update_profile") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = customColor,
                    contentColor = backgroundColor
                )
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Profile")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Profile")
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))


            // Metrics section - Podium
            val sortedOutfits = userPublicOutfits.sortedByDescending { it.likes.size }
            val top3 = sortedOutfits.take(3)

            Text(
                text = "Metrics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start),
                color = customColor
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (top3.isEmpty()) {
                Text(
                    text = "No public outfits yet",
                    style = MaterialTheme.typography.bodySmall,
                    color = customColor.copy(alpha = 0.7f)
                )
            } else {
                val firstPlaceHeight = 160.dp
                val secondPlaceHeight = 120.dp
                val thirdPlaceHeight = 80.dp

                val first = top3.getOrNull(0)
                val second = top3.getOrNull(1)
                val third = top3.getOrNull(2)

                // Wrap Row in a Box to push it to the bottom
                Box(
                    modifier = Modifier
                        .fillMaxSize(), // Fill remaining space
                    contentAlignment = Alignment.BottomCenter // Align podium at the bottom
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // 2nd place on the left (if available)
                        if (second != null) {
                            PodiumSpot(
                                outfit = second,
                                rank = "2nd",
                                platformHeight = secondPlaceHeight
                            )
                        } else {
                            Spacer(modifier = Modifier.width(100.dp))
                        }

                        // 1st place in the center (highest)
                        if (first != null) {
                            PodiumSpot(
                                outfit = first,
                                rank = "1st",
                                platformHeight = firstPlaceHeight
                            )
                        } else {
                            Spacer(modifier = Modifier.width(100.dp))
                        }

                        // 3rd place on the right (if available)
                        if (third != null) {
                            PodiumSpot(
                                outfit = third,
                                rank = "3rd",
                                platformHeight = thirdPlaceHeight
                            )
                        } else {
                            Spacer(modifier = Modifier.width(100.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EngagementStat(
    count: Int,
    label: String,
    iconColor: Color = MaterialTheme.colorScheme.onBackground
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Row for count and icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$count",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = iconColor
            )
            if (label == "likes") {
                Icon(
                    painter = painterResource(id = R.drawable.heart), // Replace with your heart icon
                    contentDescription = "Heart Icon",
                    tint = iconColor,
                    modifier = Modifier
                        .size(16.dp) // Adjust the size as needed
                        .padding(start = 4.dp) // Space between count and icon
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = customColor
        )
    }
}

@Composable
fun PodiumSpot(
    outfit: Outfit,
    rank: String,
    platformHeight: Dp
) {
    Box(
        modifier = Modifier.width(100.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // The platform
        Box(
            modifier = Modifier
                .height(platformHeight)
                .fillMaxWidth()
                .background(Color(0xff1e1e1e)), // Podium color
            contentAlignment = Alignment.Center
        ) {
            // Crown for 1st place
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (rank == "1st") {
                    Icon(
                        painter = painterResource(id = R.drawable.crown),
                        contentDescription = "Crown",
                        tint = Color(0xffffd700),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = rank,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xffffd700) // gold color for rank text
                )
            }
        }

        // The outfit card above the platform
        Column(
            modifier = Modifier
                .padding(bottom = platformHeight) // Move the card above the platform
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.placeholder_image),
                contentDescription = "Outfit Image",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .size(48.dp)
                    .padding(bottom = 4.dp)
            )

            Text(
                text = outfit.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = customColor
            )

            Text(
                text = "${outfit.likes.size} Likes",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xffff4081)
            )
        }
    }
}