package com.pyco.app.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pyco.app.viewmodels.OutfitsViewModel

class OutfitsViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OutfitsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OutfitsViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
