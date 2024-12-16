package com.pyco.app.screens.account

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import com.pyco.app.R
import com.pyco.app.components.BottomNavigationBar
import com.pyco.app.components.backgroundColor
import com.pyco.app.components.customColor
import com.pyco.app.models.ClothingItem
import com.pyco.app.models.Outfit
import com.pyco.app.models.User
import com.pyco.app.screens.home.components.requests.RequestsFeed
import com.pyco.app.screens.home.components.responses.ResponsesFeed
import com.pyco.app.screens.home.components.top_outfits.TopOutfitsFeed
import com.pyco.app.viewmodels.UserViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

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

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Edit profile button
                Button(
                    modifier = Modifier
                        .weight(1f) // Equal width for both buttons
                        .height(36.dp), // Reduce the height to make the button smaller
                    shape = RoundedCornerShape(6.dp), // Smaller corner radius
                    onClick = { navController.navigate("update_profile") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = customColor,
                        contentColor = backgroundColor
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        modifier = Modifier.size(18.dp) // Smaller icon size
                    )
                    Spacer(modifier = Modifier.width(4.dp)) // Less spacing between icon and text
                    Text(
                        text = "Edit Profile",
                        style = MaterialTheme.typography.bodySmall // Smaller text style
                    )
                }

                Spacer(modifier = Modifier.width(8.dp)) // Space between the buttons

                // Make request button
                Button(
                    modifier = Modifier
                        .weight(1f) // Equal width for both buttons
                        .height(36.dp), // Reduce the height
                    shape = RoundedCornerShape(6.dp), // Smaller corner radius
                    onClick = { navController.navigate("make_request") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = customColor,
                        contentColor = backgroundColor
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Make Request",
                        modifier = Modifier.size(18.dp) // Smaller icon size
                    )
                    Spacer(modifier = Modifier.width(4.dp)) // Less spacing
                    Text(
                        text = "Make Request",
                        style = MaterialTheme.typography.bodySmall // Smaller text style
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            // Tab Navigation
            var selectedTabIndex by remember { mutableIntStateOf(1) }
            val tabs = listOf("<3", "Your Top Outfits", "Your Requests", "Your Responses")

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
                0 -> {
                    LikedFits(

                    )
                }
                1 -> {
                    TopFits(
                        userPublicOutfits = userPublicOutfits,
                    )
                }
                2 -> {
                    YourRequests(

                    )
                }
                3 -> {
                    YourResponses(

                    )
                }
            }
        }
    }
}

// liked outfit feed section

@Composable
fun LikedFits (

) {
    Text(
        text = "Liked Outfits",
        color = customColor
    )
}

// requests you made section

@Composable
fun YourRequests (

) {
    Text(
        text = "Your Requests",
        color = customColor
    )
}

// responses you made feed section

@Composable
fun YourResponses (

) {
    Text(
        text = "your responses",
        color = customColor
    )
}


// in the name of top fits :3
@Composable
fun TopFits(
    userPublicOutfits: List<Outfit> = emptyList(),
) {
    // State to handle dialog
    var showDialog by remember { mutableStateOf(false) }
    var selectedOutfit by remember { mutableStateOf<Outfit?>(null) }

    // Metrics section - Podium
    val sortedOutfits = userPublicOutfits.sortedByDescending { it.likes.size }
    val top3 = sortedOutfits.take(3)

    Column(
        modifier = Modifier
            .fillMaxSize()
//            .padding(innerPadding)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Your Top Outfits!!",
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

            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
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
                            platformHeight = secondPlaceHeight,
                            onOutfitClick = {
                                selectedOutfit = it
                                showDialog = true
                            }
                        )
                    } else {
                        Spacer(modifier = Modifier.width(100.dp))
                    }

                    // 1st place in the center (highest)
                    if (first != null) {
                        PodiumSpot(
                            outfit = first,
                            rank = "1st",
                            platformHeight = firstPlaceHeight,
                            onOutfitClick = {
                                selectedOutfit = it
                                showDialog = true
                            }
                        )
                    } else {
                        Spacer(modifier = Modifier.width(100.dp))
                    }

                    // 3rd place on the right (if available)
                    if (third != null) {
                        PodiumSpot(
                            outfit = third,
                            rank = "3rd",
                            platformHeight = thirdPlaceHeight,
                            onOutfitClick = {
                                selectedOutfit = it
                                showDialog = true
                            }
                        )
                    } else {
                        Spacer(modifier = Modifier.width(100.dp))
                    }
                }
            }
        }
    }
    // Show dialog if needed
    if (showDialog && selectedOutfit != null) {
        OutfitPreviewDialog(
            outfit = selectedOutfit!!,
            onDismiss = {
                showDialog = false
                selectedOutfit = null
            }
        )
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
                    painter = painterResource(id = R.drawable.heart),
                    contentDescription = "Heart Icon",
                    tint = iconColor,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(start = 4.dp)
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
    platformHeight: Dp,
    onOutfitClick: (Outfit) -> Unit
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
                .fillMaxWidth()
                .clickable { onOutfitClick(outfit) }, // Click to preview
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

@Composable
fun OutfitPreviewDialog(
    outfit: Outfit,
    onDismiss: () -> Unit, // Callback to dismiss the dialog
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Horizontal scroll for clothing items
                val clothingItems = remember { mutableStateListOf<ClothingItem?>() }

                LaunchedEffect(outfit) {
                    val resolvedItems = listOf(
                        "tops" to outfit.top,
                        "bottoms" to outfit.bottom,
                        "shoes" to outfit.shoe,
                        "accessories" to outfit.accessory
                    ).map { (category, ref) ->
                        ref?.let {
                            // Correct the path and resolve to a ClothingItem object
                            val segments = it.path.split("/")
                            if (segments.size == 4) {
                                val correctedPath = "wardrobes/${segments[1]}/$category/${segments[3]}"
                                resolveClothingItem(correctedPath)
                            } else {
                                null
                            }
                        }
                    }
                    clothingItems.clear()
                    clothingItems.addAll(resolvedItems)
                }

                // Make the clothing items horizontally scrollable
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp) // Set height to ensure uniform size
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    clothingItems.forEach { item ->
                        Box(
                            modifier = Modifier
                                .size(100.dp) // Same size for all items
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (item != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(item.imageUrl),
                                    contentDescription = item.name,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .aspectRatio(1f)
                                        .clip(MaterialTheme.shapes.medium),
                                    contentScale = ContentScale.Fit
                                )
                            } else {
                                Text(
                                    text = "N/A",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }

                // Outfit Name
                Text(
                    text = outfit.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Outfit Creator
                Text(
                    text = "By: ${outfit.createdBy}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Likes
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${outfit.likes.size} Likes",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xffff4081)
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.heart),
                        contentDescription = "Heart Icon",
                        tint = Color(0xffff4081),
                        modifier = Modifier.size(16.dp).padding(start = 4.dp)
                    )
                }

                // Close Button
                Button(
                    onClick = { onDismiss() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text("Close")
                }
            }
        }
    }
}

suspend fun resolveClothingItem(path: String): ClothingItem? {
    return try {
        FirebaseFirestore.getInstance().document(path).get().await().toObject(ClothingItem::class.java)
    } catch (e: Exception) {
        Log.e("ViewModel", "Error resolving clothing item for path $path: ${e.message}")
        null
    }
}
