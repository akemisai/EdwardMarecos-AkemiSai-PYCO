package com.pyco.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pyco.app.navigation.AppNavigation
import com.pyco.app.ui.theme.PycoTheme
import com.pyco.app.viewmodels.AuthViewModel
import com.pyco.app.viewmodels.UserViewModel

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PycoTheme {
                // Provide the AuthViewModel to the navigation
                val authViewModel: AuthViewModel = viewModel()
                val userViewModel: UserViewModel = viewModel()

                AppNavigation(
                    authViewModel = authViewModel,
                    userViewModel = userViewModel
                )
            }
        }
    }
}