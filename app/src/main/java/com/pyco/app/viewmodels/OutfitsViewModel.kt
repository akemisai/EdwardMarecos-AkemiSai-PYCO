package com.pyco.app.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.DocumentReference
import com.pyco.app.models.ClothingItem
import com.pyco.app.models.Outfit
import com.pyco.app.screens.outfits.creation.components.PublicOutfitCreator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class OutfitsViewModel(
    private val userViewModel: UserViewModel,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _outfits = MutableStateFlow<List<Outfit>>(emptyList())
    val outfits: StateFlow<List<Outfit>> = _outfits

    private val _wardrobeMap = MutableStateFlow<Map<String, ClothingItem>>(emptyMap())
    val wardrobeMap: StateFlow<Map<String, ClothingItem>> = _wardrobeMap

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _publicOutfits = MutableStateFlow<List<Outfit>>(emptyList())
    val publicOutfits: StateFlow<List<Outfit>> = _publicOutfits

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            userViewModel.userProfile.collect { user ->
                user?.uid?.let { userId ->
                    fetchWardrobeItems(userId)
                    fetchUserOutfits(userId)
                } ?: run {
                    // Clear data when user is unauthenticated
                    _outfits.value = emptyList()
                    _wardrobeMap.value = emptyMap()
                }
            }
        }
    }

    private suspend fun fetchWardrobeItems(userId: String) {
        Log.d("OutfitsViewModel", "Fetching wardrobe items for user: $userId")

        val categories = listOf("tops", "bottoms", "shoes", "accessories")

        _isLoading.value = true

        try {
            categories.forEach { category ->
                val snapshot = firestore.collection("wardrobes")
                    .document(userId)
                    .collection(category)
                    .get()
                    .await()

                val wardrobeItems = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(ClothingItem::class.java)?.copy(id = doc.id)
                }

                _wardrobeMap.update { currentMap ->
                    currentMap + wardrobeItems.associateBy { it.id }
                }

                Log.d("OutfitsViewModel", "Wardrobe items fetched for $category: ${wardrobeItems.size} items")
            }
        } catch (e: Exception) {
            Log.e("OutfitsViewModel", "Error fetching wardrobe items: ${e.message}", e)
            _errorMessage.value = "Error fetching wardrobe items: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    private suspend fun fetchUserOutfits(userId: String) {
        Log.d("OutfitsViewModel", "Fetching outfits for user: $userId")
        _isLoading.value = true

        try {
            val snapshot = firestore.collection("outfits")
                .document(userId)
                .collection("user_outfits")
                .get()
                .await()

            val outfitsList = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Outfit::class.java)?.copy(id = doc.id)
            }

            _outfits.value = outfitsList
            Log.d("OutfitsViewModel", "Fetched ${outfitsList.size} outfits for user: $userId")
        } catch (e: Exception) {
            Log.e("OutfitsViewModel", "Error fetching outfits: ${e.message}", e)
            _errorMessage.value = "Error fetching outfits: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    fun addOutfit(outfit: Outfit) {
        val user = userViewModel.userProfile.value
        if (user == null) {
            _errorMessage.value = "User not authenticated. Cannot add outfit."
            return
        }

        val userId = user.uid
        val creatorName = user.displayName ?: ""
        val creatorPhotoUrl = user.photoURL ?: ""

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val newOutfitRef = firestore.collection("outfits")
                    .document(userId)
                    .collection("user_outfits")
                    .document()

                val preparedOutfit = outfit.copy(
                    id = newOutfitRef.id,
                    ownerId = userId,
                    creatorId = userId,
                    createdBy = creatorName,
                    creatorPhotoUrl = creatorPhotoUrl
                )

                firestore.runTransaction { transaction ->
                    transaction.set(newOutfitRef, preparedOutfit, SetOptions.merge())
                }.await()

                _outfits.update { currentOutfits ->
                    currentOutfits + preparedOutfit
                }

                Log.d("OutfitsViewModel", "Outfit added successfully: ${preparedOutfit.name}")

                if (preparedOutfit.isPublic) {
                    val publicOutfitCreator = PublicOutfitCreator()
                    publicOutfitCreator.createPublicOutfit(
                        preparedOutfit,
                        onSuccess = {
                            Log.d("OutfitsViewModel", "Outfit reference added to public feed!")
                        },
                        onFailure = { error ->
                            Log.e("OutfitsViewModel", "Failed to add outfit to public feed: ${error.message}")
                        }
                    )
                }
            } catch (e: Exception) {
                Log.e("OutfitsViewModel", "Error adding outfit: ${e.message}", e)
                _errorMessage.value = "Error adding outfit: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
