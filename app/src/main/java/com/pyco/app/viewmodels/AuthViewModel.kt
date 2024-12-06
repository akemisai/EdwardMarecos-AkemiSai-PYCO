package com.pyco.app.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.pyco.app.models.User

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    // LiveData for the user object
    private val _currentUserData = MutableLiveData<User?>()
    val currentUserData: LiveData<User?> = _currentUserData

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        val currentUser = auth.currentUser
        _authState.value = if (currentUser == null) {
            AuthState.Unauthenticated
        } else {
            fetchUserProfile() // fetch user doc if already signed in
            AuthState.Authenticated
        }
    }

    // email sign in ------------------------------------------------------------------------------------

    // Updates _authState to Authenticated on success
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }

        _authState.value = AuthState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                    fetchUserProfile()
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Unknown error")
                }
            }
    }

    // signup with email and password (added username)-------------------------------------------------

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
                    firebaseUser?.let { user ->
                        // Update the user's profile
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build()

                        user.updateProfile(profileUpdates)
                            .addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    // Reload user to ensure profile is updated
                                    user.reload().addOnCompleteListener {
                                        val wardrobeRef = firestore.collection("wardrobes").document()

                                        // Now save the user document in Firestore
                                        val userDocData = mapOf(
                                            "uid" to user.uid,
                                            "email" to user.email,
                                            "displayName" to (user.displayName ?: username),
                                            "photoURL" to (user.photoUrl?.toString() ?: ""),
                                            "wardrobeId" to wardrobeRef.id,
                                        )

                                        firestore.collection("users").document(user.uid)
                                            .set(userDocData, SetOptions.merge())
                                            .addOnSuccessListener {
                                                initializeWardrobe(wardrobeRef.id, user.uid)
                                                _authState.value = AuthState.Authenticated
                                                fetchUserProfile()
                                            }
                                            .addOnFailureListener { e ->
                                                _authState.value = AuthState.Error("Error saving user data: ${e.message}")
                                            }
                                    }
                                } else {
                                    _authState.value = AuthState.Error("Could not update profile")
                                }
                            }
                    } ?: run {
                        _authState.value = AuthState.Error("User creation successful but user is null")
                    }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Unknown error")
                }
            }
    }

    // Google sign-in ---------------------------------------------------------------------------------

    fun signInWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        _authState.value = AuthState.Loading

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                    fetchUserProfile()
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Unknown error")
                }
            }
    }

    // sign out ---------------------------------------------------------------------------------

    fun logOut() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
        _currentUserData.value = null
    }

    // Fetch user data from Firestore
    private fun fetchUserProfile() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    _currentUserData.value = document.toObject(User::class.java)
                } else {
                    _currentUserData.value = null
                }
            }
            .addOnFailureListener {
                _currentUserData.value = null
            }
    }

    private fun initializeWardrobe(wardrobeId: String, userId: String) {
        val wardrobeData = mapOf(
            "userId" to userId,             // Use the provided userId
            "wardrobeId" to wardrobeId,     // Use the provided wardrobeId
            "items" to emptyList<String>()  // Initial empty list of items
        )

        firestore.collection("wardrobes").document(wardrobeId)
            .set(wardrobeData)
            .addOnSuccessListener {
                Log.d("AuthViewModel", "Wardrobe document created with ID: $wardrobeId")

                // Initialize subcollections for categories
                val categories = listOf("tops", "bottoms", "shoes", "accessories")
                categories.forEach { category ->
                    firestore.collection("wardrobes").document(wardrobeId)
                        .collection(category)
                        .document() // No dummy document, just initialize the collection
                }
            }
            .addOnFailureListener { e ->
                Log.e("AuthViewModel", "Error creating wardrobe document: ${e.message}")
                _authState.value = AuthState.Error("Error initializing wardrobe: ${e.message}")
            }
    }
}

sealed class AuthState {
    data object Unauthenticated : AuthState()
    data object Authenticated : AuthState()
    data object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}