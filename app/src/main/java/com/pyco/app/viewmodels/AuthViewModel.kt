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
import com.google.firebase.firestore.SetOptions
import com.pyco.app.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _currentUserData = MutableLiveData<User?>()
    val currentUserData: LiveData<User?> = _currentUserData

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            _authState.value = AuthState.Unauthenticated
        } else {
            _authState.value = AuthState.Authenticated
            fetchUserProfile()
        }
    }

    // Email signup
    fun signup(email: String, username: String, password: String, confirmPassword: String) {
        if (email.isBlank() || username.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email, username, and password cannot be empty")
            return
        } else if (password != confirmPassword) {
            _authState.value = AuthState.Error("Passwords do not match")
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = auth.currentUser ?: throw Exception("User creation successful but user is null")

                // Update profile
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build()
                firebaseUser.updateProfile(profileUpdates).await()

                val userId = firebaseUser.uid

                firestore.collection("users").document(userId)
                    .set(mapOf("uid" to userId), SetOptions.merge()).await()

                initializeWardrobe(userId)
                _authState.postValue(AuthState.Authenticated)
                fetchUserProfile()
            } catch (e: Exception) {
                _authState.postValue(AuthState.Error(e.message ?: "Error signing up"))
            }
        }
    }

    // Sign in with Google
    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential).await()
                _authState.postValue(AuthState.Authenticated)
                fetchUserProfile()
                Log.d("AuthViewModel", "Google sign-in successful.")
            } catch (e: Exception) {
                _authState.postValue(AuthState.Error("Error during Google sign-in: ${e.message}"))
                Log.e("AuthViewModel", "Error during Google sign-in: ${e.message}")
            }
        }
    }

    // Email login
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _authState.postValue(AuthState.Authenticated)
                fetchUserProfile()
                Log.d("AuthViewModel", "Login successful.")
            } catch (e: Exception) {
                _authState.postValue(AuthState.Error("Error logging in: ${e.message}"))
                Log.e("AuthViewModel", "Login failed: ${e.message}")
            }
        }
    }

    private fun initializeWardrobe(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val wardrobeData = mapOf("userId" to userId)
                firestore.collection("wardrobes").document(userId).set(wardrobeData, SetOptions.merge()).await()

                val categories = listOf("tops", "bottoms", "shoes", "accessories")
                categories.forEach { category ->
                    firestore.collection("wardrobes")
                        .document(userId)
                        .collection(category)
                        .document("info")
                        .set(mapOf("initialized" to true)).await()
                }

                Log.d("AuthViewModel", "Wardrobe for user $userId has been initialized successfully")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error initializing wardrobe for user $userId: ${e.message}")
                _authState.postValue(AuthState.Error("Error initializing wardrobe: ${e.message}"))
            }
        }
    }

    private fun fetchUserProfile() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val document = firestore.collection("users").document(uid).get().await()
                _currentUserData.postValue(document.toObject(User::class.java))
            } catch (e: Exception) {
                _currentUserData.postValue(null)
            }
        }
    }

    fun logOut() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                auth.signOut()
                _authState.postValue(AuthState.Unauthenticated)
                _currentUserData.postValue(null)
                Log.d("AuthViewModel", "User logged out successfully.")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error logging out: ${e.message}")
                _authState.postValue(AuthState.Error("Error logging out: ${e.message}"))
            }
        }
    }
}

sealed class AuthState {
    data object Unauthenticated : AuthState()
    data object Authenticated : AuthState()
    data object Loading : AuthState()
    data class Error(val message: String, val cause: Throwable? = null) : AuthState()
}