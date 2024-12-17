package com.pyco.app.screens.home

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.pyco.app.components.BottomNavigationBar
import com.pyco.app.components.backgroundColor
import com.pyco.app.screens.home.components.HomeTopSection
import com.pyco.app.viewmodels.HomeViewModel
import com.pyco.app.viewmodels.ResponseViewModel
import com.pyco.app.viewmodels.UserViewModel

@Composable
fun HomeScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    userViewModel: UserViewModel,
    responseViewModel: ResponseViewModel
) {
    val publicOutfits by homeViewModel.publicOutfits.collectAsState()
    val userProfile by userViewModel.userProfile.collectAsState()

    Scaffold(
        containerColor = backgroundColor,
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                HomeTopSection(
                    homeViewModel = homeViewModel,
                    navController = navController,
                    userViewModel = userViewModel,
                    responseViewModel = responseViewModel
                )
            }

            LaunchedEffect(userProfile) {
                userProfile?.let {
                    Firebase.messaging.token.addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w("FCM", "Fetching FCM token failed", task.exception)
                            return@addOnCompleteListener
                        }
                        val token = task.result
                        token?.let { tk ->
                            userViewModel.updateUserFcmToken(tk)
                            Log.d("FCM", "Token updated: $tk")
                        }
                    }
                }
            }
        }
    }
}