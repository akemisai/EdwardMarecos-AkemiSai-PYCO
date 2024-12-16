package com.pyco.app.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pyco.app.models.ClothingItem
import com.pyco.app.models.Outfit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeViewModel(
    internal val userViewModel: UserViewModel,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _publicOutfits = MutableStateFlow<List<Outfit>>(emptyList())
    val publicOutfits: StateFlow<List<Outfit>> = _publicOutfits

    val userId = userViewModel.userProfile.value?.uid

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        fetchPublicOutfits()
    }

    private fun fetchPublicOutfits() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val snapshot = firestore.collection("public_outfits")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val outfits = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Outfit::class.java)
                }

                _publicOutfits.value = outfits
                Log.d("HomeViewModel", "Fetched ${outfits.size} public outfits.")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching public outfits: ${e.message}")
                _errorMessage.value = "Error fetching public outfits: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleLikeOutfit(outfitId: String, isLiked: Boolean) {
        val userId = userViewModel.userProfile.value?.uid
        if (userId == null) {
            _errorMessage.value = "User not authenticated. Cannot like outfit."
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val publicOutfitRef = firestore.collection("public_outfits").document(outfitId)

                firestore.runTransaction { transaction ->
                    val publicSnapshot = transaction.get(publicOutfitRef)
                    val currentLikes = publicSnapshot.get("likes") as? List<String> ?: emptyList()

                    val updatedLikes = if (isLiked) currentLikes + userId else currentLikes - userId
                    transaction.update(publicOutfitRef, "likes", updatedLikes)

                    val originalOutfitRef = publicSnapshot.getDocumentReference("originalOutfitRef")
                    if (originalOutfitRef != null) {
                        transaction.update(originalOutfitRef, "likes", updatedLikes)
                    }

                    // Update creator's likesCount
                    val creatorId = publicSnapshot.getString("creatorId") ?: ""
                    if (creatorId.isNotEmpty()) {
                        val creatorDocRef = firestore.collection("users").document(creatorId)
                        val incrementValue = if (isLiked) 1 else -1
                        transaction.update(creatorDocRef, "likesCount", com.google.firebase.firestore.FieldValue.increment(incrementValue.toLong()))
                    }

                    // Update the current user's likesGiven using arrayUnion/arrayRemove
                    val userDocRef = firestore.collection("users").document(userId)
                    if (isLiked) {
                        // Add this outfitId to likesGiven
                        transaction.update(userDocRef, "likesGiven", com.google.firebase.firestore.FieldValue.arrayUnion(outfitId))
                    } else {
                        // Remove this outfitId from likesGiven
                        transaction.update(userDocRef, "likesGiven", com.google.firebase.firestore.FieldValue.arrayRemove(outfitId))
                    }
                }

                // Update local publicOutfits state
                _publicOutfits.update { outfits ->
                    outfits.map {
                        if (it.id == outfitId) {
                            if (isLiked) it.copy(likes = it.likes + userId)
                            else it.copy(likes = it.likes - userId)
                        } else it
                    }
                }

                Log.d("HomeViewModel", "Toggled like for outfit: $outfitId")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error toggling like for outfit: ${e.message}")
                _errorMessage.value = "Error toggling like for outfit: ${e.message}"
            }
        }
    }

    suspend fun fetchResolvedClothingItems(outfit: Outfit): List<ClothingItem> {
        val clothingItems = mutableListOf<ClothingItem>()

        val referencesWithCategories = listOf(
            outfit.top to "tops",
            outfit.bottom to "bottoms",
            outfit.shoe to "shoes",
            outfit.accessory to "accessories"
        )

        for ((ref, category) in referencesWithCategories) {
            val item = resolveClothingItem(ref, category)
            if (item != null) clothingItems.add(item)
        }

        return clothingItems
    }

    private suspend fun resolveClothingItem(ref: DocumentReference?, category: String): ClothingItem? {
        return try {
            if (ref == null) {
                Log.e("HomeViewModel", "DocumentReference is null")
                return null
            }

            // ref.path is something like "users/{uid}/wardrobe/{itemId}"
            val segments = ref.path.split("/")
            // Expected format: ["users", "{uid}", "wardrobe", "{itemId}"]
            if (segments.size == 4 && segments[0] == "users" && segments[2] == "wardrobe") {
                val userId = segments[1]
                val itemId = segments[3]

                val resolvedPath = "wardrobes/$userId/$category/$itemId"
                Log.d("HomeViewModel", "Resolved path: $resolvedPath")
                FirebaseFirestore.getInstance().document(resolvedPath).get().await().toObject(ClothingItem::class.java)
            } else {
                Log.e("HomeViewModel", "Unexpected reference format: ${ref.path}")
                null
            }

        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error resolving clothing item: ${e.message}")
            null
        }
    }
}