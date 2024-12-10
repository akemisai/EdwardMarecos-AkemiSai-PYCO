package com.pyco.app.screens.account

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.currentCompositionErrors
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.pyco.app.R
import com.pyco.app.components.backgroundColor
import com.pyco.app.components.customColor
import com.pyco.app.viewmodels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProfileScreen(
    userViewModel: UserViewModel,
    navController: NavHostController
) {
    val userProfile by userViewModel.userProfile.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()
    val errorMessage by userViewModel.errorMessage.collectAsState()

    var newDisplayName by remember { mutableStateOf(userProfile?.displayName ?: "") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Launcher to pick an image
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { // Back Button
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = customColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor,
                    titleContentColor = customColor,
                )
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 24.dp) // Padding for bottom and sides
                    .fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        if (selectedImageUri != null) {
                            userViewModel.updateProfilePhoto(selectedImageUri!!)
                        }
                        if (newDisplayName.isNotBlank()) {
                            userViewModel.updateDisplayName(newDisplayName)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = customColor,
                        contentColor = backgroundColor
                    ),
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth(0.9f) // Make the button 90% of the available width
                        .align(Alignment.CenterHorizontally) // Center the button
                ) {
                    Text(text = "Save Changes")
                }

                Spacer(modifier = Modifier.height(32.dp)) // Space between the bottom of the screen and button
            }
        },
        containerColor = backgroundColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Display Current Profile Photo
            userProfile?.photoURL?.let { url ->
                Image(
                    painter = rememberAsyncImagePainter(model = url),
                    contentDescription = "Profile Photo",
                    modifier = Modifier
                        .size(128.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
            } ?: run {
                // Default profile picture
                Image(
                    painter = painterResource(id = R.drawable.default_profile),
                    contentDescription = "Default Profile Picture",
                    modifier = Modifier
                        .size(128.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Button to Pick New Profile Photo
            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = customColor,
                    contentColor = backgroundColor
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Change Profile Photo")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Text Field to Update Display Name
            OutlinedTextField(
                value = newDisplayName,
                onValueChange = { newDisplayName = it },
                label = { Text("Display Name") },
                colors = outlinedTextFieldColors(
                    focusedBorderColor = customColor,
                    unfocusedBorderColor = customColor,
                    cursorColor = customColor,
                    focusedLabelColor = customColor,
                    unfocusedLabelColor = customColor
                ),
                textStyle = TextStyle(color = customColor),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))



            Spacer(modifier = Modifier.height(16.dp))

            // Show Loading Indicator
            if (isLoading) {
                CircularProgressIndicator()
            }

            // Show Error Message
            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}