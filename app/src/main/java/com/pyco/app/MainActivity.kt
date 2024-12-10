package com.pyco.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pyco.app.navigation.AppNavigation
import com.pyco.app.ui.theme.PycoTheme
import com.pyco.app.viewmodels.AuthViewModel
import com.pyco.app.viewmodels.UserViewModel
import com.pyco.app.viewmodels.factories.AuthViewModelFactory

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PycoTheme {
                // Provide the AuthViewModel to the navigation
                val userViewModel: UserViewModel = viewModel()
                val authViewModel: AuthViewModel = viewModel(
                    factory = AuthViewModelFactory(userViewModel)
                )

                AppNavigation(
                    authViewModel = authViewModel,
                    userViewModel = userViewModel
                )
            }
        }
    }
}