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

        // Define categories and their respective flows
        val categories = mapOf(
            "tops" to _wardrobeMap,
            "bottoms" to _wardrobeMap,
            "shoes" to _wardrobeMap,
            "accessories" to _wardrobeMap
        )

        _isLoading.value = true

        try {
            categories.keys.forEach { category ->
                val snapshot = firestore.collection("wardrobes")
                    .document(userId)
                    .collection(category)
                    .get()
                    .await()

                val wardrobeItems = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(ClothingItem::class.java)?.copy(id = doc.id)
                }

                // Update the wardrobeMap with items from this category
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
        val userId = userViewModel.userProfile.value?.uid
        if (userId == null) {
            _errorMessage.value = "User not authenticated. Cannot add outfit."
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                // Add the outfit to Firestore and get the generated outfit ID
                val newOutfitRef = firestore.collection("outfits")
                    .document(userId)
                    .collection("user_outfits")
                    .document()

                firestore.runTransaction { transaction ->
                    transaction.set(newOutfitRef, outfit.copy(id = newOutfitRef.id), SetOptions.merge())
                }.await()

                // Update the local state with the new outfit
                _outfits.update { currentOutfits ->
                    currentOutfits + outfit.copy(id = newOutfitRef.id)
                }

                Log.d("OutfitsViewModel", "Outfit added successfully: ${outfit.name}")

                // Now handle public outfit creation if needed
                if (outfit.isPublic) {
                    val publicOutfitCreator = PublicOutfitCreator()
                    publicOutfitCreator.createPublicOutfit(
                        outfit.copy(id = newOutfitRef.id),
                        onSuccess = {
                            Log.d("OutfitsViewModel", "Outfit added to public feed!")
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


    fun deleteOutfit(outfitId: String) {
        val userId = userViewModel.userProfile.value?.uid
        if (userId == null) {
            _errorMessage.value = "User not authenticated. Cannot delete outfit."
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                firestore.collection("outfits")
                    .document(userId)
                    .collection("user_outfits")
                    .document(outfitId)
                    .delete()
                    .await()

                // Update the local state by removing the deleted outfit
                _outfits.update { currentOutfits ->
                    currentOutfits.filterNot { it.id == outfitId }
                }

                Log.d("OutfitsViewModel", "Outfit deleted successfully: $outfitId")
            } catch (e: Exception) {
                Log.e("OutfitsViewModel", "Error deleting outfit: ${e.message}", e)
                _errorMessage.value = "Error deleting outfit: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
