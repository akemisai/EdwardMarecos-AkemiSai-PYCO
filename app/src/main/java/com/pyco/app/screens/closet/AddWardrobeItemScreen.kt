package com.pyco.app.screens.closet

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.pyco.app.MainActivity
import com.pyco.app.R
import com.pyco.app.components.backgroundColor
import com.pyco.app.components.customColor
import com.pyco.app.models.ClothingItem
import com.pyco.app.models.ClothingType
import com.pyco.app.models.Colors
import com.pyco.app.models.Material
import com.pyco.app.viewmodels.ClosetViewModel
import com.pyco.app.viewmodels.UserViewModel
import com.pyco.app.viewmodels.factories.ClosetViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWardrobeItemScreen(
    navController: NavHostController,
    closetViewModel: ClosetViewModel, // Use shared ClosetViewModel
    imageUri: String? // Add imageUri parameter
) {

    // State variables for form fields
    val context = LocalContext.current as MainActivity
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(ClothingType.TOP) }
    var colors by remember { mutableStateOf(Colors.BLACK) }
    var material by remember { mutableStateOf(Material.COTTON) }
    var imageUrl by remember { mutableStateOf<String?>(null) }

    // Firebase Storage reference
    val storage = FirebaseStorage.getInstance()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val ref = storage.reference.child("wardrobes/images/${uri.lastPathSegment}")
            ref.putFile(uri).addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUri ->
                    imageUrl = downloadUri.toString()
                }
            }.addOnFailureListener {
                Log.e("AddWardrobeItem", "Error uploading image", it)
            }
        }
    }

    // Upload the captured image if imageUri is provided
    LaunchedEffect(imageUri) {
        imageUri?.let {
            val uri = Uri.parse(it)
            Log.d("AddWardrobeItem", "Parsed URI: $uri")
            val ref = storage.reference.child("wardrobes/images/${uri.lastPathSegment}")
            ref.putFile(uri).addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUri ->
                    imageUrl = downloadUri.toString()
                    Log.d("AddWardrobeItem", "Image uploaded successfully: $imageUrl")
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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor,
                    titleContentColor = customColor,
                    navigationIconContentColor = customColor
                )
            )
        },
        containerColor = backgroundColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
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

            DropdownMenuBox(
                label = "Type",
                options = ClothingType.entries,
                selectedOption = type,
                onOptionSelected = { type = it }
            )

            DropdownMenuBox(
                label = "Color",
                options = Colors.entries,
                selectedOption = colors,
                onOptionSelected = { colors = it }
            )

            DropdownMenuBox(
                label = "Material",
                options = Material.entries,
                selectedOption = material,
                onOptionSelected = { material = it }
            )
            Text(
                text = "Select Image",
                color = customColor,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .clickable { launcher.launch("image/*") }

            )

            imageUrl?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clip(MaterialTheme.shapes.medium)
                        .fillMaxWidth()
                        .height(350.dp)
                        .background(Color.Black)
                )
            }
        }
        Box(modifier = Modifier.fillMaxSize()) {
            ExtendedFloatingActionButton(
                onClick = {
                    Log.d("AddWardrobeItem", "Save Button Clicked")
                    if (name.isNotBlank() && imageUrl != null) {
                        val newItem = ClothingItem(
                            name = name,
                            type = type,
                            colour = colors,
                            material = material,
                            imageUrl = imageUrl!!
                        )

                        // Map ClothingType to subcollection name
                        val category = when (type) {
                            ClothingType.TOP -> "tops"
                            ClothingType.BOTTOM -> "bottoms"
                            ClothingType.SHOE -> "shoes"
                            ClothingType.ACCESSORY -> "accessories"
                        }

                        // Get the current user's ID directly from FirebaseAuth
                        val userId = FirebaseAuth.getInstance().currentUser?.uid
                        if (userId != null) {
                            // Add item using the ViewModel
                            closetViewModel.addClothingItem(newItem, userId, category)
                            Log.d("AddWardrobeItem", "Item Added: $newItem")
                            navController.navigateUp()
                        } else {
                            Log.e("AddWardrobeItem", "User ID is null")
                        }
                    } else {
                        Log.e("AddWardrobeItem", "Validation failed: Ensure all fields are filled.")
                    }
                },
                containerColor = customColor,
                contentColor = backgroundColor,
                content = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_save),
                        contentDescription = "Save Item",
                        tint = backgroundColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Item", color = backgroundColor)
                },
                modifier = Modifier
                    .align(BottomCenter)
                    .padding(40.dp)
            )
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
        Text(
            text = label + ":",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            ),
            color = customColor)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .clickable { expanded = true }
                .background(Color(0xfff2f2f2), shape = MaterialTheme.shapes.small)
                .padding(8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(text = selectedOption.toString(), color = backgroundColor)
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
                    },
                    modifier = Modifier
                        .background(Color(0xfff2f2f2), shape = MaterialTheme.shapes.small)
                        .padding(2.dp)
                )
            }
        }
    }
}


