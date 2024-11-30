package com.pyco.app.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.pyco.app.R

val customColor = Color(0xFFF7F7F7) // Light text color
val backgroundColor = Color(0xFF333333) // Dark background color

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = backgroundColor
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.nav_home),
                    contentDescription = "Home",
                    modifier = Modifier.size(28.dp)
                )
            },
            label = {
                Text("Home")
            },
            selected = currentRoute == "home",
            onClick = {
                navController.navigate("home") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = backgroundColor,
                unselectedIconColor = customColor,
                selectedTextColor = customColor,
                unselectedTextColor = customColor,
                indicatorColor = customColor
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.nav_closet),
                    contentDescription = "Closet",
                    modifier = Modifier.size(28.dp)
                )
            },
            label = {
                Text("Closet")
            },
            selected = currentRoute == "closet",
            onClick = {
                navController.navigate("closet") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = backgroundColor,
                unselectedIconColor = customColor,
                selectedTextColor = customColor,
                unselectedTextColor = customColor,
                indicatorColor = customColor
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(
                        id = if (currentRoute == "upload") R.drawable.nav_on_upload else R.drawable.nav_upload
                    ),
                    contentDescription = "Upload",
                    modifier = Modifier.size(28.dp)
                )
            },
            label = {
                Text("Upload")
            },
            selected = currentRoute == "upload",
            onClick = {
                navController.navigate("upload") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = backgroundColor,
                unselectedIconColor = customColor,
                selectedTextColor = customColor,
                unselectedTextColor = customColor,
                indicatorColor = customColor
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.nav_shirt),
                    contentDescription = "Outfits",
                    modifier = Modifier.size(28.dp)
                )
            },
            label = {
                Text("Outfits")
            },
            selected = currentRoute == "outfits",
            onClick = {
                navController.navigate("outfits") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = backgroundColor,
                unselectedIconColor = customColor,
                selectedTextColor = customColor,
                unselectedTextColor = customColor,
                indicatorColor = customColor
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.nav_profile),
                    contentDescription = "Profile",
                    modifier = Modifier.size(28.dp)
                )
            },
            label = {
                Text("Profile")
            },
            selected = currentRoute == "account",
            onClick = {
                navController.navigate("account") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = backgroundColor,
                unselectedIconColor = customColor,
                selectedTextColor = customColor,
                unselectedTextColor = customColor,
                indicatorColor = customColor
            )
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_6_pro", name = "fone")
@Composable
fun BottomNavigationBarPreview() {
    BottomNavigationBar(
        navController = NavHostController(LocalContext.current)
    )
}
