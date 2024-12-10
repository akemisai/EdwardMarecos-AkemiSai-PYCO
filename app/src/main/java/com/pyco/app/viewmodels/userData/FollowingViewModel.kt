package com.pyco.app.viewmodels.userData

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FollowingViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _following = MutableStateFlow<List<DocumentReference>>(emptyList())
    val following: StateFlow<List<DocumentReference>> = _following

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun getFollowing() {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.update { true }

            try {
                val document = firestore.collection("users").document(userId).get().await()
                val followingList = document.get("following") as List<DocumentReference>
                _following.update { followingList }

            } catch (e: Exception) {
                Log.e("FollowingViewModel", "Error fetching following list: ${e.message}", e)
            } finally {
                _isLoading.update { false }
            }
        }
    }
}
