package com.pyco.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pyco.app.viewmodels.UserViewModel

class ClosetViewModelFactory(
    private val userViewModel: UserViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClosetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClosetViewModel(userViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
