package com.pyco.app.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pyco.app.viewmodels.AuthViewModel
import com.pyco.app.viewmodels.UserViewModel

class AuthViewModelFactory(
    private val userViewModel: UserViewModel
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(userViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
