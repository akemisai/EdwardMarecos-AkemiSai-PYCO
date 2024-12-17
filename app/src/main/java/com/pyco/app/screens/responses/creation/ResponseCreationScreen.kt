package com.pyco.app.screens.responses.creation

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pyco.app.components.backgroundColor
import com.pyco.app.components.customColor
import com.pyco.app.models.ClothingItem
import com.pyco.app.models.Outfit
import com.pyco.app.models.Request
import com.pyco.app.screens.outfits.creation.components.ClothingItemSelector
import com.pyco.app.viewmodels.ClosetViewModel
import com.pyco.app.viewmodels.OutfitsViewModel
import com.pyco.app.viewmodels.ResponseViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResponseCreationScreen(
    navController: NavHostController,
    request: Request,
    closetViewModel: ClosetViewModel = viewModel(),
    outfitsViewModel: OutfitsViewModel = viewModel(),
    responseViewModel: ResponseViewModel = viewModel()
) {
    var ownerItems by remember { mutableStateOf<Map<String, List<ClothingItem>>>(emptyMap()) }

    var outfitName by remember { mutableStateOf("") }
    var public by remember { mutableStateOf(false) }
    var selectedTop by remember { mutableStateOf<ClothingItem?>(null) }
    var selectedBottom by remember { mutableStateOf<ClothingItem?>(null) }
    var selectedShoe by remember { mutableStateOf<ClothingItem?>(null) }
    var selectedAccessory by remember { mutableStateOf<ClothingItem?>(null) }
    var comment by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val displayName = FirebaseAuth.getInstance().currentUser?.displayName ?: "Unknown User"
    val responderId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(request.ownerId) {
        Log.d("ResponseCreationScreen", "Fetching wardrobe for owner: ${request.ownerId}")
        ownerItems = closetViewModel.fetchRequestOwnerItems(request.ownerId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Response Outfit") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor,
                    titleContentColor = customColor,
                    navigationIconContentColor = customColor
                )
            )
        },
        containerColor = backgroundColor,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (outfitName.isNotBlank() &&
                        selectedTop != null &&
                        selectedBottom != null &&
                        selectedShoe != null
                    ) {
                        val wardrobePath = "users/${request.ownerId}/wardrobe"
                        val newOutfitRef = FirebaseFirestore.getInstance().collection("outfits")
                            .document(responderId)
                            .collection("user_outfits")
                            .document()
                        Log.d("ResponseCreationScreen", "New outfit ref ID: ${newOutfitRef.id}")

                        val newOutfit = Outfit(
                            id = newOutfitRef.id,
                            name = outfitName,
                            createdBy = displayName,
                            top = FirebaseFirestore.getInstance().document("$wardrobePath/${selectedTop!!.id}"),
                            bottom = FirebaseFirestore.getInstance().document("$wardrobePath/${selectedBottom!!.id}"),
                            shoe = FirebaseFirestore.getInstance().document("$wardrobePath/${selectedShoe!!.id}"),
                            accessory = selectedAccessory?.let {
                                FirebaseFirestore.getInstance().document("$wardrobePath/${it.id}")
                            },
                            public = false
                        )

                        Log.d("ResponseCreationScreen", "Outfit ID set as: ${newOutfit.id}")

                        outfitsViewModel.addOutfit(newOutfit, request.ownerId)
                        Log.d("ResponseCreationScreen", "Outfit added to ViewModel: ${newOutfit.id}")

                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Outfit created successfully!")
                            responseViewModel.createResponse(
                                requestId = request.id,
                                title = request.title,
                                requestDescription = request.description, // Pass request description
                                responderId = responderId,
                                outfitId = newOutfit.id,
                                outfitName = outfitName,
                                comment = comment
                            )
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
                },
                containerColor = customColor,
                contentColor = backgroundColor
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
                            .padding(bottom = 16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = customColor,
                            unfocusedBorderColor = customColor,
                            cursorColor = customColor,
                            focusedPlaceholderColor = customColor,
                            unfocusedPlaceholderColor = customColor,
                            focusedTextColor = customColor,
                            unfocusedTextColor = customColor,
                            focusedLabelColor = customColor,
                            unfocusedLabelColor = customColor
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        label = { Text("Comment") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = customColor,
                            unfocusedBorderColor = customColor,
                            cursorColor = customColor,
                            focusedPlaceholderColor = customColor,
                            unfocusedPlaceholderColor = customColor,
                            focusedTextColor = customColor,
                            unfocusedTextColor = customColor,
                            focusedLabelColor = customColor,
                            unfocusedLabelColor = customColor
                        )
                    )
                }

                item {
                    Text(text = "Select Top", color = customColor, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    ClothingItemSelector(
                        items = ownerItems["tops"] ?: emptyList(),
                        selectedItem = selectedTop,
                        onItemSelected = {
                            selectedTop = it
                            Log.d("ResponseCreation", "Selected Top: ${it.id}")
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Text(text = "Select Bottom", color = customColor, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    ClothingItemSelector(
                        items = ownerItems["bottoms"] ?: emptyList(),
                        selectedItem = selectedBottom,
                        onItemSelected = {
                            selectedBottom = it
                            Log.d("ResponseCreation", "Selected Bottom: ${it.id}")
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Text(text = "Select Shoes", color = customColor, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    ClothingItemSelector(
                        items = ownerItems["shoes"] ?: emptyList(),
                        selectedItem = selectedShoe,
                        onItemSelected = {
                            selectedShoe = it
                            Log.d("ResponseCreation", "Selected Shoe: ${it.id}")
                        },
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Text(text = "Select Accessory (Optional)", color = customColor, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    ClothingItemSelector(
                        items = ownerItems["accessories"] ?: emptyList(),
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