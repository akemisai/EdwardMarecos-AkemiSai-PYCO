package com.pyco.app.screens.closet

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.storage.FirebaseStorage
import com.pyco.app.models.ClothingItem
import com.pyco.app.models.ClothingType
import com.pyco.app.models.Colors
import com.pyco.app.models.Material
import com.pyco.app.viewmodels.ClosetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWardrobeItemScreen(
    navController: NavHostController,
    closetViewModel: ClosetViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(ClothingType.TOP) }
    var colors by remember { mutableStateOf(Colors.BLACK) } // Default enum value
    var material by remember { mutableStateOf(Material.COTTON) } // Default enum value
    var imageUrl by remember { mutableStateOf<String?>(null) }

    val storage = FirebaseStorage.getInstance()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val ref = storage.reference.child("wardrobe/${uri.lastPathSegment}")
            ref.putFile(uri).addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUri ->
                    imageUrl = downloadUri.toString()
                }
            }.addOnFailureListener {
                Log.e("AddWardrobeItem", "Error uploading image", it)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Item") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            // Dropdown for Clothing Type
            DropdownMenuBox(
                label = "Type",
                options = ClothingType.entries,
                selectedOption = type,
                onOptionSelected = { type = it }
            )

            // Dropdown for Color
            DropdownMenuBox(
                label = "Color",
                options = Colors.entries,
                selectedOption = colors,
                onOptionSelected = { colors = it }
            )

            // Dropdown for Material
            DropdownMenuBox(
                label = "Material",
                options = Material.entries,
                selectedOption = material,
                onOptionSelected = { material = it }
            )

            // Image Picker
            Button(onClick = { launcher.launch("image/*") }) {
                Text("Select Image")
            }

            imageUrl?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Selected Image",
                    modifier = Modifier.size(128.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    Log.d("AddWardrobeItem", "Save Button Clicked")
                    Log.d("AddWardrobeItem", "Name: $name, Type: $type, Color: $colors, Material: $material, ImageUrl: $imageUrl")

                    if (name.isNotBlank() && imageUrl != null) {
                        val newItem = ClothingItem(
                            name = name,
                            type = type,
                            colors = colors,
                            material = material,
                            imageUrl = imageUrl!!
                        )
                        closetViewModel.addClothingItem(newItem)
                        Log.d("AddWardrobeItem", "Item Added: $newItem")
                        navController.navigateUp()
                    } else {
                        Log.e("AddWardrobeItem", "Validation failed: Ensure all fields are filled.")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Save Item", color = Color.White)
            }

        }
    }
}

@Composable
fun <T> DropdownMenuBox(
    label: String,
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall)
        Box(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 8.dp)
                .clickable { expanded = true },
            contentAlignment = Alignment.CenterStart
        ) {
            Text(text = selectedOption.toString(), color = MaterialTheme.colorScheme.onBackground)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.toString()) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

