package com.pyco.app.screens.account.others

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.pyco.app.components.backgroundColor
import com.pyco.app.components.customColor
import com.pyco.app.models.User
import com.pyco.app.screens.account.TopFits
import com.pyco.app.viewmodels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    userId: String,
    navController: NavHostController,
    userViewModel: UserViewModel = viewModel()
) {
    val currentUserId = userViewModel.userProfile.value?.uid ?: ""
    var userProfile by remember { mutableStateOf<User?>(null) }
    val userPublicOutfits by userViewModel.userPublicOutfits.collectAsState()
    var isLoading by remember { mutableStateOf(true) }

    var followerCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(userId) {
        userProfile = userViewModel.fetchUserProfileById(userId)
        followerCount = userProfile?.followers?.size ?: 0
        isLoading = false
    }

    val currentUserProfile = userViewModel.userProfile.collectAsState().value
    val isFollowing = currentUserProfile?.following?.contains(userId) == true

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(userProfile?.displayName ?: "User Profile") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor,
                    titleContentColor = customColor
                )
            )
        },
        containerColor = backgroundColor
    ) { padding ->
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            userProfile == null -> {
                Text("User not found.", modifier = Modifier.padding(padding))
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(userProfile?.photoURL ?: ""),
                        contentDescription = "User Profile Picture",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = userProfile?.displayName ?: "No Name",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Follow/Unfollow Button
                    Button(
                        onClick = {
                            userViewModel.toggleFollowUser(userId, !isFollowing)
                            if (isFollowing) {
                                followerCount -= 1 // Decrement follower count for UI feedback
                            } else {
                                followerCount += 1 // Increment follower count for UI feedback
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFollowing) Color.Gray else customColor
                        )
                    ) {
                        Text(if (isFollowing) "Unfollow" else "Follow")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Followers: ${followerCount ?: 0}")
                    Text("Following: ${userProfile?.followingCount ?: 0}")

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "User's Public Outfits",
                        style = MaterialTheme.typography.titleMedium,
                        color = customColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))

                    TopFits(userPublicOutfits = userPublicOutfits)
                }
            }
        }
    }
}
