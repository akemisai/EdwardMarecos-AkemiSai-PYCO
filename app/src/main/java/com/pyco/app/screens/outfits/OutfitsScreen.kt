package com.pyco.app.screens.outfits

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.pyco.app.components.BottomNavigationBar
import com.pyco.app.components.backgroundColor
import com.pyco.app.components.customColor
import com.pyco.app.models.ClothingItem
import com.pyco.app.navigation.Routes
import com.pyco.app.screens.outfits.components.OutfitCardGridItem
import com.pyco.app.viewmodels.OutfitsViewModel
import com.pyco.app.viewmodels.UserViewModel
import com.pyco.app.viewmodels.factories.OutfitsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitsScreen(
    navController: NavHostController,
    outfitsViewModel: OutfitsViewModel = viewModel(
        factory = OutfitsViewModelFactory(
            userViewModel = UserViewModel(),
            firestore = FirebaseFirestore.getInstance()
        )
    )
) {

    val outfits by outfitsViewModel.outfits.collectAsState()
    val wardrobeMap by outfitsViewModel.wardrobeMap.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Resolver function for DocumentReference to ClothingItem
    val resolveClothingItem: (DocumentReference?) -> ClothingItem? = { reference ->
        reference?.id?.let { wardrobeMap[it] }
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Your Outfits") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor,
                    titleContentColor = customColor,
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    navController.navigate(Routes.CREATE_OUTFIT)
                },
                containerColor = customColor,
                contentColor = backgroundColor,
                content = {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Create Outfit",
                        tint = backgroundColor,
                    )
                    Text("Create Outfit", color = backgroundColor)
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        content = { padding ->
            if (outfits.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    Text(text = "No outfits found", style = MaterialTheme.typography.bodyLarge, color = customColor)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(outfits) { outfit ->
                        OutfitCardGridItem(
                            outfit = outfit,
                            resolveClothingItem = resolveClothingItem,
                            onClick = { selectedOutfit ->
                                navController.navigate("${Routes.OUTFIT_DETAIL}/${selectedOutfit.id}")
                            }
                        )
                    }
                }
            }
        }
    )
}