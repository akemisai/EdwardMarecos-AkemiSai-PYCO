package com.pyco.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.messaging
import com.pyco.app.navigation.AppNavigation
import com.pyco.app.ui.theme.PycoTheme
import com.pyco.app.viewmodels.AuthViewModel
import com.pyco.app.viewmodels.ClosetViewModel
import com.pyco.app.viewmodels.HomeViewModel
import com.pyco.app.viewmodels.OutfitsViewModel
import com.pyco.app.viewmodels.RequestViewModel
import com.pyco.app.viewmodels.ResponseViewModel
import com.pyco.app.viewmodels.ResponseViewModelFactory
import com.pyco.app.viewmodels.UserViewModel
import com.pyco.app.viewmodels.factories.AuthViewModelFactory
import com.pyco.app.viewmodels.factories.ClosetViewModelFactory
import com.pyco.app.viewmodels.factories.HomeViewModelFactory
import com.pyco.app.viewmodels.factories.OutfitsViewModelFactory
import com.pyco.app.viewmodels.factories.RequestViewModelFactory

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PycoTheme {
                // Initialize UserViewModel
                val userViewModel: UserViewModel = viewModel()

                // Initialize AuthViewModel with its factory
                val authViewModel: AuthViewModel = viewModel(
                    factory = AuthViewModelFactory(userViewModel)
                )

                // Initialize UserViewModel with its factory
                val homeViewModel: HomeViewModel = viewModel(
                    factory = HomeViewModelFactory(
                        userViewModel,
                        FirebaseFirestore.getInstance()
                    )
                )

                // Initialize ClosetViewModel with its factory
                val closetViewModel: ClosetViewModel = viewModel(
                    factory = ClosetViewModelFactory(userViewModel)
                )

                // Initialize OutfitsViewModel with its factory
                val outfitsViewModel: OutfitsViewModel = viewModel(
                    factory = OutfitsViewModelFactory(
                        userViewModel,
                        FirebaseFirestore.getInstance()
                    )
                )

                // Initialize RequestsViewModel with its factory
                val requestViewModel: RequestViewModel = viewModel(
                    factory = RequestViewModelFactory(
                        userViewModel,
                        FirebaseFirestore.getInstance()
                    )
                )

                // Initialize ResponsesViewModel with its factory
                val responseViewModel: ResponseViewModel = viewModel(
                    factory = ResponseViewModelFactory(
                        FirebaseFirestore.getInstance(), requestViewModel, userViewModel
                    )
                )

                // Launcher to request POST_NOTIFICATIONS permission if needed
                val requestNotificationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { granted ->
                        if (granted) {
                            Log.d("MainActivity", "POST_NOTIFICATIONS permission granted.")
                        } else {
                            Log.w("MainActivity", "POST_NOTIFICATIONS permission denied.")
                        }
                    }
                )

                // Request POST_NOTIFICATIONS permission on Android 13+ if not granted
                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                            requestNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }

                // Provide all ViewModels to AppNavigation
                AppNavigation(
                    authViewModel = authViewModel,
                    userViewModel = userViewModel,
                    homeViewModel = homeViewModel,
                    closetViewModel = closetViewModel,
                    outfitsViewModel = outfitsViewModel,
                    requestViewModel = requestViewModel,
                    responseViewModel = responseViewModel
                )
            }
        }

        // Create notification channel for notifications
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "new_followers_channel",
            "New Followers",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifications for new followers"
        }

        val notificationManager: NotificationManager =
            getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}
