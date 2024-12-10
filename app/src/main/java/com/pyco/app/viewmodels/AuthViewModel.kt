package com.pyco.app.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.pyco.app.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(
    private val userViewModel: UserViewModel
) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    // Expose user's email as a property
    val userEmail: String?
        get() = auth.currentUser?.email

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        auth.currentUser?.reload()?.addOnCompleteListener {
            if (auth.currentUser == null) {
                _authState.value = AuthState.Unauthenticated
            } else {
                _authState.value = AuthState.Authenticated
                // Optionally, fetch user profile if authenticated
                auth.currentUser?.uid?.let { userId ->
                    userViewModel.fetchUserProfile(userId)
                }
            }
        }
    }

    // LOGIN WITH EMAIL
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }

        _authState.value = AuthState.Loading

        logOut() // Ensure no old user session remains

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    viewModelScope.launch(Dispatchers.IO) {
                        try {
                            auth.currentUser?.reload()?.await()
                            checkAuthStatus()
                            auth.currentUser?.uid?.let { userId ->
                                userViewModel.fetchUserProfile(userId) // Fetch the new user's profile
                            }
                            _authState.postValue(AuthState.Authenticated)
                        } catch (e: Exception) {
                            Log.e("AuthViewModel", "Error after login: ${e.message}")
                            _authState.postValue(AuthState.Error(e.message ?: "Unknown error"))
                        }
                    }
                } else {
                    Log.e("AuthViewModel", "Login failed: ${task.exception?.message}")
                    _authState.value = AuthState.Error(task.exception?.message ?: "Unknown error")
                }
            }
    }

    // SIGNUP WITH EMAIL, PASSWORD, AND USERNAME
    fun signup(email: String, username: String, password: String, confirmPassword: String) {
        if (email.isBlank() || username.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email, username, and password cannot be empty")
            return
        } else if (password != confirmPassword) {
            _authState.value = AuthState.Error("Passwords do not match")
            return
        }

        _authState.value = AuthState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build()

                        firebaseUser.updateProfile(profileUpdates)
                            .addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    Log.d("AuthViewModel", "User profile updated with displayName: $username")

                                    viewModelScope.launch(Dispatchers.IO) {
                                        try {
                                            // Create user profile in Firestore directly
                                            val user = User(
                                                uid = firebaseUser.uid,
                                                email = firebaseUser.email ?: "",
                                                displayName = username,
                                                photoURL = "",
                                                likedOutfits = emptyList(),
                                                bookmarkedOutfits = emptyList(),
                                                followers = emptyList(),
                                                following = emptyList(),
                                                likesGiven = emptyList(),
                                                likesReceived = emptyList(),
                                                followersCount = 0,
                                                followingCount = 0,
                                                likesCount = 0
                                            )
                                            firestore.collection("users").document(user.uid)
                                                .set(user)
                                                .await()
                                            Log.d("AuthViewModel", "User profile for ${user.uid} created successfully.")

                                            // Initialize wardrobe directly
                                            val wardrobeData = mapOf("userId" to user.uid)
                                            firestore.collection("wardrobes").document(user.uid)
                                                .set(wardrobeData)
                                                .await()
                                            Log.d("AuthViewModel", "Wardrobe for user ${user.uid} initialized successfully.")

                                            // Fetch the updated user profile to update the state
                                            userViewModel.fetchUserProfile(user.uid)

                                            _authState.postValue(AuthState.Authenticated)
                                        } catch (e: Exception) {
                                            Log.e("AuthViewModel", "Error during signup process: ${e.message}")
                                            _authState.postValue(AuthState.Error(e.message ?: "Failed to complete signup"))
                                        }
                                    }
                                } else {
                                    Log.e("AuthViewModel", "Failed to update user profile: ${updateTask.exception?.message}")
                                    _authState.value = AuthState.Error(updateTask.exception?.message ?: "Failed to set username")
                                }
                            }
                    } else {
                        Log.e("AuthViewModel", "Firebase user is null after signup.")
                        _authState.value = AuthState.Error("User data is unavailable.")
                    }
                } else {
                    Log.e("AuthViewModel", "Signup failed: ${task.exception?.message}")
                    _authState.value = AuthState.Error(task.exception?.message ?: "Unknown error")
                }
            }
    }

    // SIGN IN WITH GOOGLE
    fun signInWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        _authState.value = AuthState.Loading

        logOut() // Ensure no old user session remains

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    viewModelScope.launch(Dispatchers.IO) {
                        try {
                            auth.currentUser?.reload()?.await()
                            checkAuthStatus()
                            auth.currentUser?.uid?.let { userId ->
                                userViewModel.fetchUserProfile(userId)
                            }
                            _authState.postValue(AuthState.Authenticated)
                        } catch (e: Exception) {
                            Log.e("AuthViewModel", "Error during Google sign-in: ${e.message}")
                            _authState.postValue(AuthState.Error(e.message ?: "Unknown error"))
                        }
                    }
                } else {
                    Log.e("AuthViewModel", "Google sign-in failed: ${task.exception?.message}")
                    _authState.value = AuthState.Error(task.exception?.message ?: "Unknown error")
                }
            }
    }

    // LOGOUT
    fun logOut() {
        auth.signOut()
        userViewModel.clearUserData()
        _authState.value = AuthState.Unauthenticated
    }

    sealed class AuthState {
        object Unauthenticated : AuthState()
        object Authenticated : AuthState()
        object Loading : AuthState()
        data class Error(val message: String) : AuthState()
    }
}