package com.pyco.app.screens.account.others

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddReaction
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import com.pyco.app.R
import com.pyco.app.components.BottomNavigationBar
import com.pyco.app.components.backgroundColor
import com.pyco.app.components.customColor
import com.pyco.app.models.Outfit
import com.pyco.app.models.Request
import com.pyco.app.models.User
import com.pyco.app.screens.account.EngagementStat
import com.pyco.app.screens.account.TopFits
import com.pyco.app.screens.home.components.requests.RequestCard
import com.pyco.app.screens.home.components.requests.RequestDetailDialog
import com.pyco.app.viewmodels.UserViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    userId: String,
    navController: NavHostController,
    userViewModel: UserViewModel
) {
    val currentUserId = userViewModel.userProfile.value?.uid ?: ""

    // Profile and outfits state
    var userProfile by remember { mutableStateOf<User?>(null) }
    var profileUserOutfits by remember { mutableStateOf<List<Outfit>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Followers count to update UI immediately after follow/unfollow
    var followerCount by remember { mutableIntStateOf(0) }

    // Fetch user data when userId changes
    LaunchedEffect(userId) {
        isLoading = true
        try {
            userProfile = userViewModel.fetchUserProfileById(userId)
            followerCount = userProfile?.followers?.size ?: 0
            profileUserOutfits = userViewModel.fetchUserPublicOutfits(userId)
        } catch (e: Exception) {
            Log.e("UserProfileScreen", "Error fetching user profile or outfits: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    // Determine if current user is following this profile
    val currentUserProfile = userViewModel.userProfile.collectAsState().value
    var isFollowing by remember { mutableStateOf(currentUserProfile?.following?.contains(userId) == true) }
    val theyFollowMe by remember { mutableStateOf(currentUserProfile?.followers?.contains(userId) == true) }
    var isFriends by remember { mutableStateOf(isFollowing && theyFollowMe) }

    // Tabs for this profile
    val tabs = listOf("Their Requests", "Top Outfits", "Their Outfits")
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    val displayName = userProfile?.displayName ?: "User"
    val titleText = "$displayName's profile"

    Scaffold(
        containerColor = backgroundColor,
        bottomBar = { BottomNavigationBar(navController = navController) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = titleText,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = customColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = customColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor,
                    titleContentColor = customColor
                )
            )
        }
    ) { padding ->
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = customColor)
                }
            }

            userProfile == null -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text("User not found.")
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Picture
                    Image(
                        painter = if (!userProfile?.photoURL.isNullOrEmpty())
                            rememberAsyncImagePainter(userProfile?.photoURL)
                        else painterResource(id = R.drawable.default_profile),
                        contentDescription = "User Profile Picture",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Display Name
                    Text(
                        text = userProfile?.displayName ?: "No Name",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = customColor
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Follow/Friends Button (only if current user is not the profile user)
                        if (currentUserId != userId && currentUserId.isNotEmpty()) {
                            Button(
                                onClick = {
                                    userViewModel.toggleFollowUser(userId, !isFollowing)
                                    if (isFollowing) {
                                        followerCount -= 1
                                    } else {
                                        followerCount += 1
                                    }
                                    isFollowing = !isFollowing
                                    if (theyFollowMe) { isFriends = !isFriends }
                                },
                                modifier = Modifier.height(36.dp), // Shorter button height
                                shape = RoundedCornerShape(6.dp), // Rectangle with 6.dp corner radius
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = when {
                                        isFriends -> Color(0xffffd700) // Gold for "Friends"
                                        isFollowing -> Color(0xff89cff0) // Light blue for "Following"
                                        theyFollowMe -> Color.Gray // Gray for "Follow Back"
                                        else -> customColor // Default for "Follow"
                                    },
                                    contentColor = when {
                                        isFriends -> backgroundColor // Black text for "Friends"
                                        isFollowing -> customColor // Custom color text
                                        theyFollowMe -> customColor // Custom color text
                                        else -> backgroundColor // Default white text
                                    }
                                )
                            ) {
                                Text(
                                    text = when {
                                        isFriends -> "Friends" // Mutual follow
                                        isFollowing -> "Following" // You follow them
                                        theyFollowMe -> "Follow Back" // They follow you
                                        else -> "Follow" // Default
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Spacer to create space between the button and the icon
                            Spacer(modifier = Modifier.size(8.dp))

                            // Icon based on follow conditions
                            if (theyFollowMe) {
                                Icon(
                                    imageVector = if (isFriends) Icons.Default.AddReaction else Icons.Default.Person, // Icon for Friends or Follow Back
                                    contentDescription = if (isFriends) "Friends" else "They Follow You",
                                    tint = if (isFriends) Color(0xffffd700) else customColor, // Gold for Friends, custom for Follow Back
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Engagement Stats
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp), // Reduced padding
                        horizontalArrangement = Arrangement.SpaceEvenly, // Distribute evenly with less space
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        EngagementStat(
                            count = followerCount,
                            label = "followers",
                            iconColor = customColor,
                            textSize = MaterialTheme.typography.bodyMedium, // Use a smaller text style
                            spacing = 2.dp // Reduced space between count and label
                        )
                        EngagementStat(
                            count = userProfile?.followingCount ?: 0,
                            label = "following",
                            iconColor = customColor,
                            textSize = MaterialTheme.typography.bodyMedium,
                            spacing = 2.dp
                        )
                        EngagementStat(
                            count = userProfile?.likesCount ?: 0,
                            label = "likes",
                            iconColor = Color(0xffff1e1e),
                            textSize = MaterialTheme.typography.bodyMedium,
                            spacing = 2.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()

                    // Tabs
                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        containerColor = backgroundColor,
                        contentColor = customColor,
                        modifier = Modifier.fillMaxWidth(),
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

                    // Tab Pages
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        when (page) {
                            0 -> UserRequestsForProfile(
                                ownerId = userId,
                                navController = navController,
                                userViewModel = userViewModel,
                                currentUserId = currentUserId
                            )
                            1 -> TopFits(userPublicOutfits = profileUserOutfits)
                            2 -> TheirResponses()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserRequestsForProfile(
    ownerId: String,
    navController: NavHostController,
    userViewModel: UserViewModel,
    currentUserId: String?
) {
    var requestsList by remember { mutableStateOf<List<Request>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var selectedRequest by remember { mutableStateOf<Request?>(null) }

    LaunchedEffect(ownerId) {
        try {
            val db = FirebaseFirestore.getInstance()
            val result = db.collection("requests")
                .whereEqualTo("ownerId", ownerId)
                .get()
                .await()

            requestsList = result.toObjects(Request::class.java)
        } catch (e: Exception) {
            errorMessage = "Error fetching requests: ${e.message}"
            Log.e("UserRequestsForProfile", errorMessage ?: "Unknown error")
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Their Requests",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = customColor,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when {
            isLoading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = customColor)
            }

            !errorMessage.isNullOrEmpty() -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = errorMessage ?: "Something went wrong.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            requestsList.isEmpty() -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "This user has made no requests.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(requestsList) { request ->
                        RequestCard(
                            request = request,
                            userViewModel = userViewModel,
                            onCardClick = { selectedRequest = request }
                        )
                    }
                }
            }
        }
    }

    selectedRequest?.let { request ->
        if (currentUserId != null) {
            RequestDetailDialog(
                request = request,
                onDismiss = { selectedRequest = null },
                navController = navController,
                currentUserId = currentUserId
            )
        }
    }
}

@Composable
fun TheirResponses() {
    Text(
        text = "Their responses",
        color = customColor,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(16.dp)
    )
}
