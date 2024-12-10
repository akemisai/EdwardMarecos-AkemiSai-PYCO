package com.pyco.app.screens.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults.outlinedTextFieldColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.pyco.app.viewmodels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    authViewModel: AuthViewModel,
    navController: NavHostController
) {
    val authState by authViewModel.authState.observeAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope() // Remember a coroutine scope

    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // colors
    val customColor = Color(0xFFF7F7F7)
    val backgroundColor = Color(0xFF333333)
    val signUpButtonColor = Color(0xffFFD700)

    LaunchedEffect(authState) {
        when (authState) {
            is AuthViewModel.AuthState.Authenticated -> {
                navController.navigate("home") {
                    popUpTo("signup") { inclusive = true }
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
                value = username,
                onValueChange = { username = it },
                label = { Text("Username", color = customColor) },
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
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password", color = customColor) },
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

            // sign up button

            TextButton(
                onClick = { authViewModel.signup(email, username, password, confirmPassword) },
                enabled = true,
                modifier = Modifier
                    .offset(x = -90.dp) // my plan was to offset by half the width of the password field i did (240.dp / 2) then adjusted as needed
                    .align(Alignment.CenterHorizontally) // Align to the left under the password input
            ) {
                Text(
                    text ="Sign Up",
                    color = Color.White,
                    fontSize = 16.sp,
                    textDecoration = TextDecoration.Underline,
                )
            }
            Spacer(modifier = Modifier.height(111.dp))

            // "Already have an account? Sign In"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Already have an account?",
                    color = customColor,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(onClick = { navController.navigate("login") }) {
                    Text(
                        text = "Sign In",
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