package com.pyco.app.screens.requests.creation

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.navigation.NavHostController
import com.pyco.app.components.BottomNavigationBar
import com.pyco.app.components.backgroundColor
import com.pyco.app.components.customColor
import com.pyco.app.models.Tags
import com.pyco.app.viewmodels.RequestViewModel
import com.pyco.app.viewmodels.UserViewModel
import kotlinx.coroutines.launch

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

    val coroutineScope = rememberCoroutineScope() // Create a coroutine scope
    val snackbarHostState = remember { SnackbarHostState() }

    var title by remember { mutableStateOf("") } // State for title input
    var description by remember { mutableStateOf("") }

    // Selected Tags
    val selectedTags = remember { mutableStateListOf<Tags>() }
    val allTags = Tags.entries // Predefined tags

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
            title = title,
            description = description,
            ownerId = ownerId,
            ownerName = ownerName,
            ownerPhotoUrl = ownerPhotoUrl,
            tags = selectedTags.map { it.displayName } // Convert selected tags to strings
        )
    }

    // If there’s an errorMessage, show a toast
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    // UI
    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Create Request") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = customColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor,
                    titleContentColor = customColor,
                )
            )
        },

        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    createRequest() // create the request
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Request created successfully!")
                    }
                    navController.navigateUp() //leave page
                },
                containerColor = customColor,
                contentColor = backgroundColor,
                content = {
                    Icon(
                        Icons.Filled.AutoAwesome,
                        contentDescription = "Create Request",
                        tint = Color(0xffffd700),
                    )
                    Text(
                        text = "Create Request",
                        color = backgroundColor,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            // Title Section
            Text(
                text = "Request Title:",
                color = customColor,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Enter a title for your request") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = customColor,
                    unfocusedContainerColor = customColor,
                    focusedIndicatorColor = customColor,
                    unfocusedIndicatorColor = Color.Gray,
                    cursorColor = customColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, customColor, RoundedCornerShape(8.dp))
                    .padding(2.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // description section
            Text(
                text = "Request Description:",
                color = customColor,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Enter a description for your request") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = customColor,
                    unfocusedContainerColor = customColor,
                    focusedIndicatorColor = customColor,
                    unfocusedIndicatorColor = Color.Gray,
                    cursorColor = customColor
                ),
                maxLines = 6,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .border(1.dp, customColor, RoundedCornerShape(8.dp))
                    .padding(2.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tags Section
            Text(
                text = "Tags:",
                color = customColor,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            TagsSelectionSection(
                allTags = allTags,
                selectedTags = selectedTags
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator()
            }
        }
    }
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
