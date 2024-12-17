package com.pyco.app.screens.reponses.creation

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pyco.app.models.ClothingItem
import com.pyco.app.models.Outfit
import com.pyco.app.models.Request
import com.pyco.app.screens.outfits.creation.components.ClothingItemSelector
import com.pyco.app.viewmodels.ClosetViewModel
import com.pyco.app.viewmodels.OutfitsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResponseCreationScreen(
    navController: NavHostController,
    request: Request,
    closetViewModel: ClosetViewModel = viewModel(),
    outfitsViewModel: OutfitsViewModel = viewModel()
) {
    val tops by closetViewModel.tops.collectAsState()
    val bottoms by closetViewModel.bottoms.collectAsState()
    val shoes by closetViewModel.shoes.collectAsState()
    val accessories by closetViewModel.accessories.collectAsState()

    var outfitName by remember { mutableStateOf("") }
    var public by remember { mutableStateOf(false) }
    var selectedTop by remember { mutableStateOf<ClothingItem?>(null) }
    var selectedBottom by remember { mutableStateOf<ClothingItem?>(null) }
    var selectedShoe by remember { mutableStateOf<ClothingItem?>(null) }
    var selectedAccessory by remember { mutableStateOf<ClothingItem?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val displayName = FirebaseAuth.getInstance().currentUser?.displayName ?: "Unknown User"

    LaunchedEffect(request.ownerId) {
        Log.d("ResponseCreationScreen", "Fetching wardrobe for owner: ${request.ownerId}")
        closetViewModel.fetchClothingItems(request.ownerId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Response Outfit") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (outfitName.isNotBlank() &&
                        selectedTop != null &&
                        selectedBottom != null &&
                        selectedShoe != null
                    ) {
                        val wardrobePath = "users/${request.ownerId}/wardrobe"

                        val newOutfit = Outfit(
                            name = outfitName,
                            createdBy = displayName,
                            top = FirebaseFirestore.getInstance().document("$wardrobePath/${selectedTop!!.id}"),
                            bottom = FirebaseFirestore.getInstance().document("$wardrobePath/${selectedBottom!!.id}"),
                            shoe = FirebaseFirestore.getInstance().document("$wardrobePath/${selectedShoe!!.id}"),
                            accessory = selectedAccessory?.let {
                                FirebaseFirestore.getInstance().document("$wardrobePath/${it.id}")
                            },
                            public = public
                        )

                        outfitsViewModel.addOutfit(newOutfit)

                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Outfit created successfully!")
                            navController.navigateUp()
                        }
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Please fill in all required fields.")
                        }
                    }
                },
                content = {
                    Icon(Icons.Filled.Save, contentDescription = "Save Outfit")
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        content = { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = outfitName,
                        onValueChange = { outfitName = it },
                        label = { Text("Outfit Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                }

                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = public,
                            onCheckedChange = { public = it }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Make this outfit public")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Text(text = "Select Top", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    ClothingItemSelector(
                        items = tops,
                        selectedItem = selectedTop,
                        onItemSelected = {
                            selectedTop = it
                            Log.d("ResponseCreation", "Selected Top: ${it.id}")
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Text(text = "Select Bottom", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    ClothingItemSelector(
                        items = bottoms,
                        selectedItem = selectedBottom,
                        onItemSelected = {
                            selectedBottom = it
                            Log.d("ResponseCreation", "Selected Bottom: ${it.id}")
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Text(text = "Select Shoes", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    ClothingItemSelector(
                        items = shoes,
                        selectedItem = selectedShoe,
                        onItemSelected = {
                            selectedShoe = it
                            Log.d("ResponseCreation", "Selected Shoe: ${it.id}")
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Text(text = "Select Accessory (Optional)", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    ClothingItemSelector(
                        items = accessories,
                        selectedItem = selectedAccessory,
                        onItemSelected = {
                            selectedAccessory = it
                            Log.d("ResponseCreation", "Selected Accessory: ${it.id}")
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    )
}