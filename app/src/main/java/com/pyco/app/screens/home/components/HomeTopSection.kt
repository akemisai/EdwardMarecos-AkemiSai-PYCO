package com.pyco.app.screens.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pyco.app.R
import com.pyco.app.components.backgroundColor
import com.pyco.app.components.customColor
import com.pyco.app.screens.home.components.requests.RequestsFeed
import com.pyco.app.screens.home.components.responses.ResponsesFeed
import com.pyco.app.screens.home.components.top_outfits.TopOutfitsFeed
import com.pyco.app.viewmodels.HomeViewModel

@Composable
fun HomeTopSection(
    navController: NavHostController? = null,
    homeViewModel: HomeViewModel = viewModel()
) {

    val publicOutfits by homeViewModel.publicOutfits.collectAsState()

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
                Icon(
                    painter = painterResource(id = R.drawable.mis_chat), // chat icon
                    contentDescription = "Chat",
                    tint = customColor,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(top = 2.dp)
                        .padding(end = 4.dp)
                )
                Icon(
                    painter = painterResource(id = R.drawable.mis_alert), // notification icon
                    contentDescription = "Notification",
                    tint = customColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tab Navigation
        var selectedTabIndex by remember { mutableIntStateOf(0) }
        val tabs = listOf("Requests", "Top Outfits", "Responses")

        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = backgroundColor,
            contentColor = customColor,
            indicator = { tabPositions ->
                SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = customColor.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Display Content Based on Selected Tab
        when (selectedTabIndex) {
            0 -> RequestsFeed(requestViewModel = viewModel())
            1 -> TopOutfitsFeed(outfits = publicOutfits)
            2 -> ResponsesFeed()
        }
    }
}

@Preview(showBackground = true, device = "spec:parent=pixel_6_pro", name = "HomeTopSection Preview")
@Composable
fun HomeTopSectionPreview() {
    HomeTopSection()
}
