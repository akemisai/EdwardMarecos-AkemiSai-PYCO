@file:Suppress("DEPRECATION")

package com.pyco.app.screens.authentication

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.pyco.app.R
import com.pyco.app.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

val dmSerifDisplay = FontFamily(Font(R.font.dm_serif_display_regular))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    navController: NavHostController,
) {
    val authState by authViewModel.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope() // Remember a coroutine scope

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current

    // colors
    val customColor = Color(0xFFF7F7F7)
    val backgroundColor = Color(0xFF333333)
    val signUpButtonColor = Color(0xffFFD700)

    // Google Sign-In launcher
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken != null) {
                authViewModel.signInWithGoogle(idToken)
            } else {
                // Launch a coroutine to show the snackbar
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Failed to get ID Token")
                }
            }
        } catch (e: ApiException) {
            // Launch a coroutine to show the snackbar
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Google Sign-In failed: ${e.message}")
            }
        }
    }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthViewModel.AuthState.Authenticated -> {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }
            is AuthViewModel.AuthState.Error -> {
                snackbarHostState.showSnackbar((authState as AuthViewModel.AuthState.Error).message)
            }
            else -> Unit
        }
    }

    // ui layout

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
        ) {
            SnackbarHost(hostState = snackbarHostState)

            Spacer(modifier = Modifier.height(175.dp))

            Text(
                text = "PYCO",
                style = TextStyle(
                    fontSize = 64.sp,
                    fontFamily = dmSerifDisplay,
                    fontWeight = FontWeight(400),
                    color = Color(0xFFF7F7F7),
                    textAlign = TextAlign.Center,
                )
            )
            Spacer(modifier = Modifier.height(62.dp))

            // text inputs

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = customColor) },
                colors = outlinedTextFieldColors(
                    focusedBorderColor = customColor,
                    unfocusedBorderColor = customColor,
                    cursorColor = customColor,
                    focusedLabelColor = customColor, // for focused label color
                    unfocusedLabelColor = customColor // for unfocused label color
                ),
                textStyle = TextStyle(color = customColor), // Set text color here
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(240.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = customColor) },
                visualTransformation = PasswordVisualTransformation(),
                colors = outlinedTextFieldColors(
                    focusedBorderColor = customColor,
                    unfocusedBorderColor = customColor,
                    cursorColor = customColor,
                    focusedLabelColor = customColor, // for focused label color
                    unfocusedLabelColor = customColor // for unfocused label color
                ),
                textStyle = TextStyle(color = customColor), // Set text color here
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(240.dp)
            )

            // sign in button

            TextButton(
                onClick = { authViewModel.login(email, password) },
                enabled = true,
                modifier = Modifier
                    .offset(x = -90.dp) // my plan was to offset by half the width of the password field i did (240.dp / 2) then adjusted as needed
                    .align(Alignment.CenterHorizontally) // Align to the left under the password input
            ) {
                Text(
                    text ="Sign in",
                    color = Color.White,
                    fontSize = 16.sp,
                    textDecoration = TextDecoration.Underline,
                )
            }
            Spacer(modifier = Modifier.height(175.dp))

            // bottom portion of screen

            // Divider with "or sign in with" text
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Divider(
                    color = customColor,
                    thickness = 1.dp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "or sign in with",
                    color = customColor,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Divider(
                    color = customColor,
                    thickness = 1.dp,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Social media icons for sign in (draw circles for now)

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // google sign in button first
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google Sign In",
                    modifier = Modifier
                        .size(48.dp)
                        .padding(8.dp)
                        .background(color = customColor, shape = CircleShape) // Make the image round
                        .clip(CircleShape)
                        .padding(4.dp)
                        .clickable {
                            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken(context.getString(R.string.default_web_client_id))
                                .requestEmail()
                                .build()

                            val googleSignInClient = GoogleSignIn.getClient(context, gso)
                            val signInIntent = googleSignInClient.signInIntent
                            googleSignInLauncher.launch(signInIntent)
                        }
                )

//                // instagram sign in button next
//                androidx.compose.foundation.Image(
//                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_instagram),
//                    contentDescription = "Instagram Sign In",
//                    modifier = Modifier
//                        .size(48.dp)
//                        .padding(8.dp)
//                        .background(color = customColor, shape = CircleShape) // Make the image round
//                        .clip(CircleShape)
//                        .padding(5.dp)
//                        .clickable {
//                            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                                .requestIdToken(context.getString(R.string.default_web_client_id))
//                                .requestEmail()
//                                .build()
//
//                            val googleSignInClient = GoogleSignIn.getClient(context, gso)
//                            val signInIntent = googleSignInClient.signInIntent
//                            googleSignInLauncher.launch(signInIntent)
//                        }
//                )
//
//                // then x sign in button
//                androidx.compose.foundation.Image(
//                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_x),
//                    contentDescription = "Google Sign In",
//                    modifier = Modifier
//                        .size(48.dp)
//                        .padding(8.dp)
//                        .background(color = customColor, shape = CircleShape) // Make the image round
//                        .clip(CircleShape)
//                        .padding(6.dp)
//                        .clickable {
//                            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                                .requestIdToken(context.getString(R.string.default_web_client_id))
//                                .requestEmail()
//                                .build()
//
//                            val googleSignInClient = GoogleSignIn.getClient(context, gso)
//                            val signInIntent = googleSignInClient.signInIntent
//                            googleSignInLauncher.launch(signInIntent)
//                        }
//                )
//
//                // lastly sign in with tiktok button
//                androidx.compose.foundation.Image(
//                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_tiktok),
//                    contentDescription = "Instagram Sign In",
//                    modifier = Modifier
//                        .size(48.dp)
//                        .padding(8.dp)
//                        .background(color = customColor, shape = CircleShape) // Make the image round
//                        .clip(CircleShape)
//                        .padding(5.dp)
//                        .clickable {
//                            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                                .requestIdToken(context.getString(R.string.default_web_client_id))
//                                .requestEmail()
//                                .build()
//
//                            val googleSignInClient = GoogleSignIn.getClient(context, gso)
//                            val signInIntent = googleSignInClient.signInIntent
//                            googleSignInLauncher.launch(signInIntent)
//                        }
//                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // "Don't have an account? Sign Up"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Don't have an account?",
                    color = customColor,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(onClick = { navController.navigate("signup") }) {
                    Text(
                        text = "Sign Up",
                        color = signUpButtonColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
        }
    }
}