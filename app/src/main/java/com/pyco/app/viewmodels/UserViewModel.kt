package com.pyco.app.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
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

    private val _userPublicOutfits = MutableStateFlow<List<Outfit>>(emptyList())
    val userPublicOutfits: StateFlow<List<Outfit>> = _userPublicOutfits

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
            _isLoading.value = true
            try {
                firestore.collection("users").document(uid).set(user).await()
                initializeWardrobe(uid)
                fetchUserProfile(uid)
                Log.d("UserViewModel", "User profile created for $uid")
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error creating user profile: ${e.message}")
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchUserProfile(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val doc = firestore.collection("users").document(userId).get().await()
                if (doc.exists()) {
                    val user = doc.toObject(User::class.java)
                    _userProfile.value = user
                    user?.uid?.let { fetchUserPublicOutfits(it) }
                    Log.d("UserViewModel", "Fetched user profile for $userId")
                } else {
                    _errorMessage.value = "User profile not found."
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error fetching user profile: ${e.message}")
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun fetchUserProfileById(userId: String): User? {
        return try {
            val snapshot = firestore.collection("users").document(userId).get().await()
            snapshot.toObject(User::class.java)
        } catch (e: Exception) {
            Log.e("UserViewModel", "Error fetching user profile: ${e.message}")
            null
        }
    }

    // Follow or unfollow a user
    fun toggleFollowUser(targetUserId: String, isFollowing: Boolean) {
        val currentUserId = getCurrentUserId() ?: run {
            _errorMessage.value = "User not authenticated. Cannot follow user."
            return
        }

        if (currentUserId == targetUserId) {
            _errorMessage.value = "You cannot follow yourself."
            return // Prevent following oneself
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                firestore.runTransaction { transaction ->
                    // **References for the two user documents**
                    val currentUserRef = firestore.collection("users").document(currentUserId)
                    val targetUserRef = firestore.collection("users").document(targetUserId)

                    // **Update the "followers" list and "followersCount" for the target user**
                    if (isFollowing) {
                        transaction.update(targetUserRef, "followers", FieldValue.arrayUnion(currentUserId))
                        transaction.update(targetUserRef, "followersCount", FieldValue.increment(1))
                    } else {
                        transaction.update(targetUserRef, "followers", FieldValue.arrayRemove(currentUserId))
                        transaction.update(targetUserRef, "followersCount", FieldValue.increment(-1))
                    }

                    // **Update the "following" list and "followingCount" for the current user**
                    if (isFollowing) {
                        transaction.update(currentUserRef, "following", FieldValue.arrayUnion(targetUserId))
                        transaction.update(currentUserRef, "followingCount", FieldValue.increment(1))
                    } else {
                        transaction.update(currentUserRef, "following", FieldValue.arrayRemove(targetUserId))
                        transaction.update(currentUserRef, "followingCount", FieldValue.increment(-1))
                    }
                }

                // **Update local state for the current user (profile of the person following)**
                _userProfile.update { currentUser ->
                    currentUser?.copy(
                        following = if (isFollowing) currentUser.following + targetUserId else currentUser.following - targetUserId,
                        followingCount = if (isFollowing) currentUser.followingCount + 1 else currentUser.followingCount - 1
                    )
                }

                Log.d("UserViewModel", "Toggled follow for user: $targetUserId. Following status: $isFollowing")
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error toggling follow for user: ${e.message}")
                _errorMessage.value = "Error toggling follow for user: ${e.message}"
            }
        }
    }

    // fetch the followers or following lists of a user
    suspend fun getFollowers(userId: String): List<User> {
        return try {
            // get the "followers" list directly from the user document
            val userDoc = firestore.collection("users").document(userId).get().await()

            // Extract the list of follower IDs (if it exists) or return an empty list
            val followerIds = userDoc.get("followers") as? List<String> ?: emptyList()

            // fetch User objects for each follower ID using fetchUserProfileById
            followerIds.mapNotNull { fetchUserProfileById(it) }
        } catch (e: Exception) {
            Log.e("UserViewModel", "Error fetching followers for $userId: ${e.message}")
            emptyList()
        }
    }

    suspend fun getFollowing(userId: String): List<User> {
        return try {
            // get the "following" list directly from the user document
            val userDoc = firestore.collection("users").document(userId).get().await()

            // Extract the list of following IDs (if it exists) or return an empty list
            val followingIds = userDoc.get("following") as? List<String> ?: emptyList()

            // fetch User objects for each following ID using fetchUserProfileById
            followingIds.mapNotNull { fetchUserProfileById(it) }
        } catch (e: Exception) {
            Log.e("UserViewModel", "Error fetching following for $userId: ${e.message}")
            emptyList()
        }
    }

    suspend fun fetchUserPublicOutfits(userId: String): List<Outfit> {
        return try {
            val snapshot = firestore.collection("public_outfits")
                .whereEqualTo("creatorId", userId)
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject(Outfit::class.java) }
        } catch (e: Exception) {
            Log.e("UserViewModel", "Error fetching public outfits for $userId: ${e.message}")
            emptyList()
        }
    }

    private suspend fun initializeWardrobe(userId: String) {
        try {
            firestore.collection("wardrobes").document(userId)
                .set(mapOf("userId" to userId))
                .await()
        } catch (e: Exception) {
            Log.e("UserViewModel", "Error initializing wardrobe: ${e.message}")
            _errorMessage.value = e.message
        }
    }

    fun updateDisplayName(newDisplayName: String) {
        val userId = getCurrentUserId() ?: return

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val user = auth.currentUser
                if (user != null) {
                    user.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(newDisplayName).build()).await()
                    firestore.collection("users").document(userId).update("displayName", newDisplayName).await()
                    _userProfile.update { it?.copy(displayName = newDisplayName) }
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error updating display name: ${e.message}")
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfilePhoto(imageUri: Uri) {
        val userId = getCurrentUserId() ?: return

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val storageRef = storage.reference.child("profile_photos/$userId.jpg")
                storageRef.putFile(imageUri).await()
                val downloadUrl = storageRef.downloadUrl.await().toString()

                auth.currentUser?.updateProfile(
                    UserProfileChangeRequest.Builder().setPhotoUri(Uri.parse(downloadUrl)).build()
                )?.await()

                firestore.collection("users").document(userId)
                    .update("photoURL", downloadUrl)
                    .await()

                _userProfile.update { it?.copy(photoURL = downloadUrl) }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error updating profile photo: ${e.message}")
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    // for user notifs
    fun updateUserFcmToken(token: String) {
        val userId = getCurrentUserId() ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                firestore.collection("users").document(userId)
                    .update("fcmToken", token)
                    .await()
                Log.d("UserViewModel", "FCM token updated for user: $userId")
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error updating FCM token: ${e.message}")
            }
        }
    }


    fun clearUserData() {
        _userProfile.value = null
        _isLoading.value = false
        _errorMessage.value = null
    }

    private fun getCurrentUserId(): String? = auth.currentUser?.uid
}
