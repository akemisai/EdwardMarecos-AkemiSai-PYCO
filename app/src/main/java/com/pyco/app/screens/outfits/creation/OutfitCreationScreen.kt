package com.pyco.app.screens.outfits.creation

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pyco.app.components.backgroundColor
import com.pyco.app.components.customColor
import com.pyco.app.models.ClothingItem
import com.pyco.app.models.Outfit
import com.pyco.app.models.Tags
import com.pyco.app.screens.outfits.creation.components.ClothingItemSelector
import com.pyco.app.viewmodels.ClosetViewModel
import com.pyco.app.viewmodels.OutfitsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitCreationScreen(
    navController: NavHostController,
    closetViewModel: ClosetViewModel,
    outfitsViewModel: OutfitsViewModel
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
    val displayName = FirebaseAuth.getInstance().currentUser?.displayName ?: "Unknown User" // Fetch displayName

    // Selected Tags
    val selectedTags = remember { mutableStateListOf<Tags>() }
    val allTags = Tags.entries // Predefined tags

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
                        val wardrobePath = "users/${FirebaseAuth.getInstance().currentUser?.uid}/wardrobe"

                        val newOutfit = Outfit(
                            name = outfitName,
                            createdBy = displayName,
                            top = FirebaseFirestore.getInstance().document("$wardrobePath/${selectedTop!!.id}"),
                            bottom = FirebaseFirestore.getInstance().document("$wardrobePath/${selectedBottom!!.id}"),
                            shoe = FirebaseFirestore.getInstance().document("$wardrobePath/${selectedShoe!!.id}"),
                            accessory = selectedAccessory?.let {
                                FirebaseFirestore.getInstance().document("$wardrobePath/${it.id}")
                            },
                            public = public,
                            tags = selectedTags.map { it.displayName } // Ensure tags are included
                        )

                        outfitsViewModel.addOutfit(newOutfit, FirebaseAuth.getInstance().currentUser?.uid ?: "")  // make new outfit

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
                item{
                    Text(
                        text = "Tags:",
                        color = customColor,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    com.pyco.app.screens.outfits.creation.TagsSelectionSection(
                        allTags = allTags,
                        selectedTags = selectedTags
                    )
                }
            }
        }
    )
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagsSelectionSection(
    allTags: List<Tags>,
    selectedTags: MutableList<Tags>
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            allTags.forEach { tag ->
                FilterChip(
                    selected = selectedTags.contains(tag),
                    onClick = {
                        if (selectedTags.contains(tag)) {
                            selectedTags.remove(tag)
                        } else {
                            selectedTags.add(tag)
                        }
                    },
                    label = {
                        Text(
                            text = tag.displayName,
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color.Gray,
                        selectedLabelColor = backgroundColor,
                        containerColor = customColor,
                        labelColor = Color.Gray
                    )
                )
            }
        }
    }
}
