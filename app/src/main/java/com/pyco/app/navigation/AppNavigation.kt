package com.pyco.app.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pyco.app.models.Request
import com.pyco.app.screens.account.AccountScreen
import com.pyco.app.screens.account.UpdateProfileScreen
import com.pyco.app.screens.account.components.FollowOrFollowing
import com.pyco.app.screens.account.others.UserProfileScreen
import com.pyco.app.screens.authentication.LoginScreen
import com.pyco.app.screens.authentication.SignUpScreen
import com.pyco.app.screens.closet.AddWardrobeItemScreen
import com.pyco.app.screens.closet.ClosetScreen
import com.pyco.app.screens.home.HomeScreen
import com.pyco.app.screens.outfits.OutfitDetailScreen
import com.pyco.app.screens.outfits.OutfitsScreen
import com.pyco.app.screens.outfits.creation.OutfitCreationScreen
import com.pyco.app.screens.responses.creation.ResponseCreationScreen
import com.pyco.app.screens.requests.creation.MakeRequestScreen
import com.pyco.app.screens.responses.components.ResponsesListScreen
import com.pyco.app.screens.upload.UploadScreen
import com.pyco.app.viewmodels.AuthViewModel
import com.pyco.app.viewmodels.ClosetViewModel
import com.pyco.app.viewmodels.HomeViewModel
import com.pyco.app.viewmodels.OutfitsViewModel
import com.pyco.app.viewmodels.RequestViewModel
import com.pyco.app.viewmodels.ResponseViewModel
import com.pyco.app.viewmodels.UserViewModel

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    homeViewModel: HomeViewModel,
    closetViewModel: ClosetViewModel,
    outfitsViewModel: OutfitsViewModel,
    requestViewModel: RequestViewModel,
    responseViewModel: ResponseViewModel,
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
                homeViewModel = homeViewModel,
                userViewModel = userViewModel,
                responseViewModel = responseViewModel
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
                navController = navController,
                authViewModel = authViewModel
            )
        }

        // follow / followers list display screen
        // this guy chonky cause i want it to go to the same screen just a different section
        composable(
            route = "${Routes.FOLLOW_OR_FOLLOWING}/{type}/{userId}",
            arguments = listOf(
                navArgument("type") { type = NavType.StringType },
                navArgument("userId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "followers"
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            FollowOrFollowing(
                navController = navController,
                userViewModel = userViewModel,
                selectedTab = type, // Pass type to set the initial tab
                userId = userId
            )
        }

        // Add Update Profile Screen
        composable(Routes.UPDATE_PROFILE) {
            UpdateProfileScreen(
                userViewModel = userViewModel,
                navController = navController
            )
        }
        composable("${Routes.USER_PROFILE}/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            UserProfileScreen(
                userId = userId,
                navController = navController,
                userViewModel = userViewModel
            )
        }

        // Wardrobe related navigation
        composable(
            route = "${Routes.ADD_WARDROBE_ITEM}?imageUri={imageUri}",
            arguments = listOf(navArgument("imageUri") { type = NavType.StringType; nullable = true })
        ) { backStackEntry ->
            val imageUri = backStackEntry.arguments?.getString("imageUri")
            AddWardrobeItemScreen(
                navController = navController,
                closetViewModel = closetViewModel,
                imageUri = imageUri
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
            MakeRequestScreen(
                requestViewModel = requestViewModel,
                userViewModel = userViewModel,
                navController = navController
            )
        }

        // Response related Nav
        composable(
            route = "${Routes.CREATE_RESPONSE}?requestId={requestId}&ownerId={ownerId}",
            arguments = listOf(navArgument("requestId") { type = NavType.StringType; nullable = true }, navArgument("ownerId") { type = NavType.StringType; nullable = true })
        ) { backStackEntry ->
            val requestId = backStackEntry.arguments?.getString("requestId")
            val ownerId = backStackEntry.arguments?.getString("ownerId")
            Log.d("AppNavigation", "Navigating to ResponseCreationScreen with requestId: $requestId and ownerId: $ownerId")
            ResponseCreationScreen(
                responseViewModel = responseViewModel,
                navController = navController,
                request = Request(id = requestId ?: "", ownerId = ownerId ?: ""),
                closetViewModel = closetViewModel,
                outfitsViewModel = outfitsViewModel
            )
        }

        // ResponseListScreen Nav
        composable(
            route ="${Routes.RESPONSES_LIST}/{requestId}/{title}",
            arguments = listOf(
                navArgument("requestId") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val requestId = backStackEntry.arguments?.getString("requestId") ?: ""
            val title = backStackEntry.arguments?.getString("title") ?: ""
            Log.d("AppNavigation", "Navigating to ResponseListScreen with requestId: $requestId")
            ResponsesListScreen(
                responseViewModel = responseViewModel,
                navController = navController,
                requestId = requestId,
                title = title
            )
        }
    }
}
