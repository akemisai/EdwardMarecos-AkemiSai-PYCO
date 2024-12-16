package com.pyco.app.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.pyco.app.models.Outfit
import com.pyco.app.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserViewModel : ViewModel() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        val userId = getCurrentUserId()
        if (userId != null) {
            fetchUserProfile(userId)
        } else {
            Log.e("UserViewModel", "No authenticated user found during initialization.")
        }
    }

    fun createUserProfile(uid: String, email: String, displayName: String) {
        val user = User(
            uid = uid,
            email = email,
            displayName = displayName,
            photoURL = "",
            bookmarkedOutfits = emptyList(),
            followers = emptyList(),
            following = emptyList(),
            likesGiven = emptyList(),
            followersCount = 0,
            followingCount = 0,
            likesCount = 0
        )

        viewModelScope.launch {
            _isLoading.update { true }
            try {
                // Create or update user profile in Firestore
                firestore.collection("users").document(uid)
                    .set(user)
                    .await()
                Log.d("UserViewModel", "User profile for $uid created successfully.")

                // Initialize wardrobe
                initializeWardrobe(uid)

                // Fetch the user profile to update the state
                fetchUserProfile(uid)
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error creating user profile: ${e.message}")
                _errorMessage.update { e.message }
            } finally {
                _isLoading.update { false }
            }
        }
    }

    fun fetchUserProfile(userId: String) {
        viewModelScope.launch {
            _isLoading.update { true }
            try {
                val document = firestore.collection("users").document(userId).get().await()
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    _userProfile.update { user }

                    // Fetch public outfits for this user after we have the user profile
                    user?.uid?.let { uid ->
                        fetchUserPublicOutfits(uid)
                    }

                    Log.d("UserViewModel", "Fetched user profile for $userId successfully.")
                } else {
                    Log.e("UserViewModel", "User profile not found for $userId.")
                    _errorMessage.update { "User profile not found." }
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error fetching user profile for $userId: ${e.message}")
                _errorMessage.update { e.message }
            } finally {
                _isLoading.update { false }
                Log.d("UserViewModel", "Finished fetching user profile for $userId.")
            }
        }
    }

    // a user that is not yourself
    suspend fun fetchUserProfileById(userId: String): User? {
        return try {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get()
                .await()
            snapshot.toObject(User::class.java)
        } catch (e: Exception) {
            Log.e("UserViewModel", "Error fetching user profile: ${e.message}")
            null
        }
    }


    private val _userPublicOutfits = MutableStateFlow<List<Outfit>>(emptyList())
    val userPublicOutfits: StateFlow<List<Outfit>> = _userPublicOutfits

    fun fetchUserPublicOutfits(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.update { true }
            try {
                val snapshot = firestore.collection("public_outfits")
                    .whereEqualTo("creatorId", userId)
                    .get()
                    .await()

                val outfits = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Outfit::class.java)
                }

                _userPublicOutfits.value = outfits
                Log.d("UserViewModel", "Fetched ${outfits.size} public outfits for user: $userId")
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error fetching user public outfits: ${e.message}")
                _errorMessage.update { "Error fetching user public outfits: ${e.message}" }
            } finally {
                _isLoading.update { false }
            }
        }
    }

    private suspend fun initializeWardrobe(userId: String) {
        try {
            val wardrobeData = mapOf("userId" to userId)
            firestore.collection("wardrobes").document(userId)
                .set(wardrobeData)
                .await()
            Log.d("UserViewModel", "Wardrobe for user $userId initialized successfully.")
        } catch (e: Exception) {
            Log.e("UserViewModel", "Error initializing wardrobe for user $userId: ${e.message}")
            _errorMessage.update { e.message }
        }
    }

    fun addFollower(userId: String, followerId: String) {
        viewModelScope.launch {
            _isLoading.update { true }
            try {
                val followerRef = firestore.collection("users").document(followerId)
                val userDocRef = firestore.collection("users").document(userId)

                // Batch write to add follower and increment follower count atomically
                firestore.runBatch { batch ->
                    batch.update(userDocRef, "followers", com.google.firebase.firestore.FieldValue.arrayUnion(followerRef))
                    batch.update(userDocRef, "followersCount", com.google.firebase.firestore.FieldValue.increment(1))
                }.await()

                Log.d("UserViewModel", "Follower $followerId added to user $userId successfully.")
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error adding follower $followerId to user $userId: ${e.message}")
                _errorMessage.update { e.message }
            } finally {
                _isLoading.update { false }
            }
        }
    }

    // update profile
    fun updateDisplayName(newDisplayName: String) {
        val userId = getCurrentUserId()
        if (userId == null) {
            _errorMessage.update { "User not authenticated." }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.update { true }
            try {
                // Update FirebaseAuth profile
                val user = auth.currentUser
                if (user != null) {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(newDisplayName)
                        .build()

                    user.updateProfile(profileUpdates).await()

                    // Update Firestore user document
                    firestore.collection("users").document(userId)
                        .update("displayName", newDisplayName)
                        .await()

                    // Update local state
                    _userProfile.update { currentUser ->
                        currentUser?.copy(displayName = newDisplayName)
                    }

                    Log.d("UserViewModel", "Display name updated successfully.")
                } else {
                    throw Exception("Firebase user is null.")
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error updating display name: ${e.message}")
                _errorMessage.update { e.message }
            } finally {
                _isLoading.update { false }
            }
        }
    }

    fun updateProfilePhoto(imageUri: Uri) {
        val userId = getCurrentUserId()
        if (userId == null) {
            _errorMessage.update { "User not authenticated." }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.update { true }
            try {
                // Define storage reference
                val storageRef = storage.reference.child("profile_photos/$userId.jpg")

                // Upload the file
                storageRef.putFile(imageUri).await()

                // Get the download URL
                val downloadUrl = storageRef.downloadUrl.await().toString()

                // Update FirebaseAuth profile
                val user = auth.currentUser
                if (user != null) {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setPhotoUri(Uri.parse(downloadUrl))
                        .build()

                    user.updateProfile(profileUpdates).await()

                    // Update Firestore user document
                    firestore.collection("users").document(userId)
                        .update("photoURL", downloadUrl)
                        .await()

                    // Update local state
                    _userProfile.update { currentUser ->
                        currentUser?.copy(photoURL = downloadUrl)
                    }

                    Log.d("UserViewModel", "Profile photo updated successfully.")
                } else {
                    throw Exception("Firebase user is null.")
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error updating profile photo: ${e.message}")
                _errorMessage.update { e.message }
            } finally {
                _isLoading.update { false }
            }
        }
    }

    fun clearUserData() {
        _userProfile.update { null }
        _isLoading.update { false }
        _errorMessage.update { null }
    }

    private fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}