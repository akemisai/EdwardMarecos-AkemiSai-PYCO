package com.pyco.app.screens.requests.creation

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pyco.app.viewmodels.RequestViewModel
import com.pyco.app.viewmodels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MakeRequestScreen(
    requestViewModel: RequestViewModel,
    userViewModel: UserViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    val userProfile = userViewModel.userProfile.collectAsState().value
    val isLoading by requestViewModel.isLoading.collectAsState()
    val errorMessage by requestViewModel.errorMessage.collectAsState()

    var description by remember { mutableStateOf("") }

    // If user not available, show a message or redirect
    if (userProfile == null) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("User not authenticated. Cannot create request.")
            Button(onClick = { navController.navigate("home") }) {
                Text("Go Home")
            }
        }
        return
    }

    val ownerId = userProfile.uid
    val ownerName = userProfile.displayName ?: ""
    val ownerPhotoUrl = userProfile.photoURL ?: ""

    // Handle create request action
    fun createRequest() {
        if (description.isBlank()) {
            Toast.makeText(context, "Description cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        requestViewModel.createRequest(
            description = description,
            ownerId = ownerId,
            ownerName = ownerName,
            ownerPhotoUrl = ownerPhotoUrl
        )

        // After creation, you might want to navigate back or show a success message.
        // Since createRequest is async, you can track completion by checking for errors and loading state.
    }

    // If there’s an errorMessage, show a toast
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    // UI
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Request") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text("Request Description:")
            Spacer(modifier = Modifier.height(8.dp))
            BasicTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant)
                    .padding(8.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { createRequest() }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Button(onClick = { createRequest() }) {
                    Text("Create Request")
                }
            }
        }
    }
}