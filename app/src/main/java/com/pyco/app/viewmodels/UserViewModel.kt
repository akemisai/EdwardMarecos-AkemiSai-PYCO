package com.pyco.app.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.pyco.app.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // StateFlow to hold user profile data
    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile

    // StateFlow for loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // StateFlow for error messages
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        fetchUserProfile()
    }

    /**
     * Fetch the user profile from Firestore
     */
    fun fetchUserProfile() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e("UserViewModel", "User is not authenticated.")
            _errorMessage.update { "User is not authenticated." }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.update { true }
            try {
                val document = firestore.collection("users").document(userId).get().await()
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    _userProfile.update { user }
                    Log.d("UserViewModel", "Fetched user profile: $user")
                } else {
                    Log.e("UserViewModel", "User profile does not exist.")
                    _errorMessage.update { "User profile does not exist." }
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error fetching user profile: ${e.message}", e)
                _errorMessage.update { "Error fetching user profile: ${e.message}" }
            } finally {
                _isLoading.update { false }
            }
        }
    }

    /**
     * Update the user profile in Firestore
     */
    fun updateUserProfile(updatedUser: User) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e("UserViewModel", "User is not authenticated.")
            _errorMessage.update { "User is not authenticated." }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.update { true }
            try {
                firestore.collection("users")
                    .document(userId)
                    .set(updatedUser, SetOptions.merge()) // Use SetOptions.merge() to avoid overwriting entire document
                    .await()

                _userProfile.update { updatedUser }
                Log.d("UserViewModel", "User profile updated successfully: $updatedUser")
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error updating user profile: ${e.message}", e)
                _errorMessage.update { "Error updating user profile: ${e.message}" }
            } finally {
                _isLoading.update { false }
            }
        }
    }
}