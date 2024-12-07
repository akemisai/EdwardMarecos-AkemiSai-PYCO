package com.pyco.app.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pyco.app.viewmodels.ClosetViewModel
import com.pyco.app.viewmodels.UserViewModel

/**
 * Factory class for creating instances of ClosetViewModel.
 *
 * i chose to make this because i was running into an error
 * trying to make the wardrobe, it wasn't being created (cannot create
 * instance of class closet view model. closet view model has dependencies
 * we need to pass in and we do this here
 *
 * wrote note because this part was really having me confused :3
 */

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

