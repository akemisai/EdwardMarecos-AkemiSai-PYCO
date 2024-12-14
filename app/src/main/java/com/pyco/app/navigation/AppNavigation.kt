package com.pyco.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pyco.app.screens.account.AccountScreen
import com.pyco.app.screens.account.UpdateProfileScreen
import com.pyco.app.screens.account.components.SettingsScreen
import com.pyco.app.screens.authentication.LoginScreen
import com.pyco.app.screens.authentication.SignUpScreen
import com.pyco.app.screens.closet.AddWardrobeItemScreen
import com.pyco.app.screens.closet.ClosetScreen
import com.pyco.app.screens.home.HomeScreen
import com.pyco.app.screens.outfits.OutfitDetailScreen
import com.pyco.app.screens.outfits.OutfitsScreen
import com.pyco.app.screens.outfits.creation.OutfitCreationScreen
import com.pyco.app.screens.requests.creation.MakeRequestScreen
import com.pyco.app.screens.upload.UploadScreen
import com.pyco.app.viewmodels.AuthViewModel
import com.pyco.app.viewmodels.ClosetViewModel
import com.pyco.app.viewmodels.HomeViewModel
import com.pyco.app.viewmodels.OutfitsViewModel
import com.pyco.app.viewmodels.RequestViewModel
import com.pyco.app.viewmodels.UserViewModel
import com.pyco.app.viewmodels.factories.ClosetViewModelFactory
import com.pyco.app.viewmodels.factories.OutfitsViewModelFactory

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    homeViewModel: HomeViewModel,
    closetViewModel: ClosetViewModel,
    outfitsViewModel: OutfitsViewModel
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.LOGIN) {

        // Auth navigation
        composable(Routes.LOGIN) {
            LoginScreen(
                authViewModel = authViewModel,
                navController = navController
            )
        }
        composable(Routes.SIGNUP) {
            SignUpScreen(
                authViewModel = authViewModel,
                navController = navController
            )
        }

        // Main navigation
        composable(Routes.HOME) {
            HomeScreen(
                navController = navController,
                homeViewModel = homeViewModel
            )
        }
        composable(Routes.CLOSET) {
            ClosetScreen(
                navController = navController,
                closetViewModel = closetViewModel
            )
        }
        composable(Routes.UPLOAD) {
            UploadScreen(navController = navController)
        }
        composable(Routes.OUTFITS) {
            OutfitsScreen(
                navController = navController,
                outfitsViewModel = outfitsViewModel
            )
        }
        composable(Routes.ACCOUNT) {
            AccountScreen(
                userViewModel = userViewModel,
                navController = navController
            )
        }

        // Account related navigation
        composable(Routes.SETTINGS) {
            SettingsScreen(
                authViewModel = authViewModel,
                navController = navController
            )
        }
        // Add Update Profile Screen
        composable(Routes.UPDATE_PROFILE) {
            UpdateProfileScreen(
                userViewModel = userViewModel,
                navController = navController
            )
        }

        // Wardrobe related navigation
        composable(Routes.ADD_WARDROBE_ITEM) {
            AddWardrobeItemScreen(
                navController = navController,
                closetViewModel = closetViewModel
            )
        }

        // Outfit related navigation
        composable(Routes.CREATE_OUTFIT) {
            OutfitCreationScreen(
                navController = navController,
                closetViewModel = closetViewModel,
                outfitsViewModel = outfitsViewModel
            )
        }

        composable("${Routes.OUTFIT_DETAIL}/{outfitId}") { backStackEntry ->
            val outfitId = backStackEntry.arguments?.getString("outfitId") ?: "default_id"
            OutfitDetailScreen(
                navController = navController,
                outfitId = outfitId,
                outfitsViewModel = outfitsViewModel,
                closetViewModel = closetViewModel // Pass ClosetViewModel here
            )
        }

        // Request related navigation
        composable(Routes.MAKE_REQUEST) {
            val requestViewModel: RequestViewModel = viewModel()
            MakeRequestScreen(
                requestViewModel = requestViewModel,
                navController = navController
            )
        }
    }
}
