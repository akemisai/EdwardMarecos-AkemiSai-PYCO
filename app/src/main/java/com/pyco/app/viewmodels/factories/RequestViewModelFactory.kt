package com.pyco.app.viewmodels.factories


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.pyco.app.viewmodels.RequestViewModel
import com.pyco.app.viewmodels.UserViewModel

class RequestViewModelFactory(
    private val userViewModel: UserViewModel,
    private val firestore: FirebaseFirestore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RequestViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RequestViewModel(userViewModel, firestore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
