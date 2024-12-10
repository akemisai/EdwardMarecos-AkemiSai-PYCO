package com.pyco.app.screens.account

import androidx.compose.foundation.Image
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.pyco.app.R
import com.pyco.app.components.BottomNavigationBar
import com.pyco.app.models.User
import com.pyco.app.viewmodels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    userViewModel: UserViewModel = viewModel(),
    navController: NavHostController
) {
    val userProfile by userViewModel.userProfile.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { BottomNavigationBar(navController = navController) },
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "PYCO", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                },
                actions = {
                    IconButton(onClick = { /* TODO: Navigate to notifications */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
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
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Followers, Following, and Likes stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                userProfile?.let { profile ->
                    EngagementStat(count = profile.followersCount, label = "followers")
                    EngagementStat(count = profile.followingCount, label = "following")
                    EngagementStat(count = profile.likesCount, label = "likes", iconColor = Color.Red)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Edit profile button
            Button(
                onClick = { navController.navigate("update_profile") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Profile")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Profile")
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))


            // Metrics section
            Text(
                text = "Metrics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Example for highlights/outfits display
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Replace with actual data for outfits or popular posts
                MetricCard(title = "Fall Fit", stats = "3000 Likes 432 Comments")
                MetricCard(title = "Fit 2", stats = "2000 Likes")
                MetricCard(title = "Fit 3", stats = "1000 Likes")
            }
        }
    }
}

@Composable
fun EngagementStat(count: Int, label: String, iconColor: Color = MaterialTheme.colorScheme.onBackground) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$count",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = iconColor
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun MetricCard(title: String, stats: String) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.placeholder_image), // Replace with actual resource
                contentDescription = "Placeholder Icon",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(48.dp)
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stats,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}
