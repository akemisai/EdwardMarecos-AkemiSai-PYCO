package com.pyco.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.pyco.app.navigation.AppNavigation
import com.pyco.app.ui.theme.PycoTheme
import com.pyco.app.viewmodels.AuthViewModel
import com.pyco.app.viewmodels.ClosetViewModel
import com.pyco.app.viewmodels.HomeViewModel
import com.pyco.app.viewmodels.OutfitsViewModel
import com.pyco.app.viewmodels.UserViewModel
import com.pyco.app.viewmodels.factories.AuthViewModelFactory
import com.pyco.app.viewmodels.factories.ClosetViewModelFactory
import com.pyco.app.viewmodels.factories.HomeViewModelFactory
import com.pyco.app.viewmodels.factories.OutfitsViewModelFactory

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

                // Provide all ViewModels to AppNavigation
                AppNavigation(
                    authViewModel = authViewModel,
                    userViewModel = userViewModel,
                    homeViewModel = homeViewModel,
                    closetViewModel = closetViewModel,
                    outfitsViewModel = outfitsViewModel
                )
            }
        }
    }
}