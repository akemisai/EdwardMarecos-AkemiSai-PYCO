package com.pyco.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pyco.app.screens.account.AccountScreen
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
import com.pyco.app.viewmodels.OutfitsViewModel
import com.pyco.app.viewmodels.RequestViewModel

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable(Routes.LOGIN) {
            LoginScreen(
                authViewModel = authViewModel,
                navController = navController,
            )
        }
        composable(Routes.SIGNUP) {
            SignUpScreen(
                authViewModel = authViewModel,
                navController = navController
            )
        }
        composable(Routes.HOME) {
            HomeScreen(
                navController = navController
            )
        }
        composable(Routes.CLOSET) {
            ClosetScreen(
                navController = navController
            )
        }
        composable(Routes.UPLOAD) {
            UploadScreen(
                navController = navController
            )
        }
        composable(Routes.OUTFITS) {
            OutfitsScreen(
                navController = navController
            )
        }
        composable(Routes.ACCOUNT) {
            AccountScreen(
                authViewModel = authViewModel,
                navController = navController
            )
        }

        // account related navigation
        composable(Routes.SETTINGS) {
            SettingsScreen(
                authViewModel = authViewModel,
                navController = navController
            )
        }

        // wardrobe related navigation
        composable(Routes.ADD_WARDROBE_ITEM) {
            AddWardrobeItemScreen(
                navController = navController
            )
        }

        // outfit related navigation
        composable(Routes.CREATE_OUTFIT) {
            OutfitCreationScreen(
                navController = navController,
                closetViewModel = ClosetViewModel(),
                outfitsViewModel = OutfitsViewModel()
            )
        }
        composable("${Routes.OUTFIT_DETAIL}/{outfitId}") { backStackEntry ->
            val outfitId = backStackEntry.arguments?.getString("outfitId")
            OutfitDetailScreen(
                outfitId = outfitId,
                outfitsViewModel = OutfitsViewModel(),
                resolveClothingItem = { reference ->
                    OutfitsViewModel().wardrobeMap.collectAsState().value[reference?.id]
                }
            )
        }

        // request related navigation
        composable(Routes.MAKE_REQUEST) {
            MakeRequestScreen(
                requestViewModel = RequestViewModel(),
                navController = navController
            )
        }
    }
}