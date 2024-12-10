package com.pyco.app.viewmodels.userData

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FollowersViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _followers = MutableStateFlow<List<DocumentReference>>(emptyList())
    val followers: StateFlow<List<DocumentReference>> = _followers

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun followUser(followingUserId: String) {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.update { true }

            try {
                // Add the current user to the following list of the target user
                firestore.collection("users").document(followingUserId)
                    .update("followers", FieldValue.arrayUnion(userId))
                    .await()

                // Add the target user to the following list of the current user
                firestore.collection("users").document(userId)
                    .update("following", FieldValue.arrayUnion(followingUserId))
                    .await()

            } catch (e: Exception) {
                Log.e("FollowersViewModel", "Error following user: ${e.message}", e)
            } finally {
                _isLoading.update { false }
            }
        }
    }
}

