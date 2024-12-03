package com.pyco.app.screens.outfits.creation

import android.util.Log
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pyco.app.models.ClothingItem
import com.pyco.app.models.Outfit
import com.pyco.app.screens.outfits.creation.components.ClothingItemSelector
import com.pyco.app.viewmodels.ClosetViewModel
import com.pyco.app.viewmodels.OutfitsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitCreationScreen(
    navController: NavHostController,
    closetViewModel: ClosetViewModel = viewModel(),
    outfitsViewModel: OutfitsViewModel = viewModel()
) {
    val tops by closetViewModel.tops.collectAsState()
    val bottoms by closetViewModel.bottoms.collectAsState()
    val shoes by closetViewModel.shoes.collectAsState()
    val accessories by closetViewModel.accessories.collectAsState()

    var outfitName by remember { mutableStateOf("") }
    var selectedTop by remember { mutableStateOf<ClothingItem?>(null) }
    var selectedBottom by remember { mutableStateOf<ClothingItem?>(null) }
    var selectedShoe by remember { mutableStateOf<ClothingItem?>(null) }
    var selectedAccessory by remember { mutableStateOf<ClothingItem?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Outfit") },
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
                    // Validate inputs
                    if (outfitName.isNotBlank() &&
                        selectedTop != null &&
                        selectedBottom != null &&
                        selectedShoe != null
                    ) {
                        val userId = FirebaseAuth.getInstance().currentUser?.uid
                        if (userId != null) {
                            val wardrobePath = "users/$userId/wardrobe"

                            // Debugging logs for path
                            Log.d("OutfitCreation", "Top Path: $wardrobePath/${selectedTop!!.id}")
                            Log.d("OutfitCreation", "Bottom Path: $wardrobePath/${selectedBottom!!.id}")
                            Log.d("OutfitCreation", "Shoe Path: $wardrobePath/${selectedShoe!!.id}")
                            selectedAccessory?.let {
                                Log.d("OutfitCreation", "Accessory Path: $wardrobePath/${it.id}")
                            }

                            // Correctly construct DocumentReferences
                            val newOutfit = Outfit(
                                name = outfitName,
                                top = FirebaseFirestore.getInstance().document("$wardrobePath/${selectedTop!!.id}"),
                                bottom = FirebaseFirestore.getInstance().document("$wardrobePath/${selectedBottom!!.id}"),
                                shoe = FirebaseFirestore.getInstance().document("$wardrobePath/${selectedShoe!!.id}"),
                                accessory = selectedAccessory?.let {
                                    FirebaseFirestore.getInstance().document("$wardrobePath/${it.id}")
                                }
                            )

                            Log.d("OutfitCreation", "Saving Outfit: $newOutfit")
                            outfitsViewModel.addOutfit(
                                name = outfitName,
                                topRef = newOutfit.top,
                                bottomRef = newOutfit.bottom,
                                shoeRef = newOutfit.shoe,
                                accessoryRef = newOutfit.accessory
                            )
                        }

                        // Feedback & Navigation
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Outfit created successfully!")
                            delay(1500L) // Delay to show message before navigation
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
                    Text(text = "Select Top", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    ClothingItemSelector(
                        items = tops,
                        selectedItem = selectedTop,
                        onItemSelected = {
                            selectedTop = it
                            Log.d("OutfitCreation", "Selected Top: ${it.id}")
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
                            Log.d("OutfitCreation", "Selected Bottom: ${it.id}")
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
                            Log.d("OutfitCreation", "Selected Shoe: ${it.id}")
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
                            Log.d("OutfitCreation", "Selected Accessory: ${it.id}")
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    )
}
