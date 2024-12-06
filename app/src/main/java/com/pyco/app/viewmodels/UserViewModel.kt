package com.pyco.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pyco.app.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log

class UserViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // StateFlow to hold user profile data
    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile

    // StateFlow for loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchUserProfile()
    }

    // Function to fetch the user profile from Firestore
    fun fetchUserProfile() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e("UserViewModel", "User is not authenticated.")
            return
        }

        _isLoading.value = true
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    _userProfile.value = user
                    Log.d("UserViewModel", "Fetched user profile: $user")
                } else {
                    Log.e("UserViewModel", "User profile does not exist.")
                }
                _isLoading.value = false
            }
            .addOnFailureListener { exception ->
                Log.e("UserViewModel", "Error fetching user profile: ${exception.message}")
                _isLoading.value = false
            }
    }

    // Function to update user profile fields in Firestore
    fun updateUserProfile(updatedUser: User) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e("UserViewModel", "User is not authenticated.")
            return
        }

        firestore.collection("users").document(userId)
            .set(updatedUser)
            .addOnSuccessListener {
                _userProfile.value = updatedUser
                Log.d("UserViewModel", "User profile updated: $updatedUser")
            }
            .addOnFailureListener { exception ->
                Log.e("UserViewModel", "Error updating user profile: ${exception.message}")
            }
    }
}
