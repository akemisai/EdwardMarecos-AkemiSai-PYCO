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
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val userId = auth.currentUser?.uid

    private val _outfits = MutableStateFlow<List<Outfit>>(emptyList())
    val outfits: StateFlow<List<Outfit>> = _outfits

    private val _wardrobeMap = MutableStateFlow<Map<String, ClothingItem>>(emptyMap())
    val wardrobeMap: StateFlow<Map<String, ClothingItem>> = _wardrobeMap

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        fetchWardrobeItems()
        fetchUserOutfits()
    }

    private fun fetchWardrobeItems() {
        if (userId == null) {
            _errorMessage.update { "User not authenticated. Cannot fetch wardrobe items." }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val categories = listOf("tops", "bottoms", "shoes", "accessories")
                categories.forEach { category ->
                    val snapshot = firestore.collection("wardrobes")
                        .document(userId)
                        .collection(category)
                        .get()
                        .await()

                    val wardrobeItems = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(ClothingItem::class.java)?.copy(id = doc.id)
                    }.associateBy { it.id }

                    _wardrobeMap.update { it + wardrobeItems }
                    Log.d("OutfitsViewModel", "Wardrobe items fetched for $category")
                }
            } catch (e: Exception) {
                Log.e("OutfitsViewModel", "Error fetching wardrobe items: ${e.message}", e)
                _errorMessage.update { "Error fetching wardrobe items: ${e.message}" }
            }
        }
    }

    private fun fetchUserOutfits() {
        if (userId == null) {
            _errorMessage.update { "User not authenticated. Cannot fetch outfits." }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val snapshot = firestore.collection("outfits")
                    .document(userId)
                    .collection("user_outfits")
                    .get()
                    .await()

                val outfitsList = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Outfit::class.java)?.copy(id = doc.id)
                }
                _outfits.update { outfitsList }
            } catch (e: Exception) {
                Log.e("OutfitsViewModel", "Error fetching outfits: ${e.message}", e)
                _errorMessage.update { "Error fetching outfits: ${e.message}" }
            }
        }
    }

    fun addOutfit(outfit: Outfit) {
        if (userId == null) {
            _errorMessage.update { "User not authenticated. Cannot add outfit." }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
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
                    publicOutfitCreator.createPublicOutfit(outfit.copy(id = newOutfitRef.id),
                        onSuccess = {
                            // Show success snack bar
                            Log.d("OutfitsViewModel", "Outfit added to public feed!")
                        },
                        onFailure = { error ->
                            Log.e("OutfitsViewModel", "Failed to add outfit to public feed: ${error.message}")
                        })
                }
            } catch (e: Exception) {
                Log.e("OutfitsViewModel", "Error adding outfit: ${e.message}", e)
                _errorMessage.update { "Error adding outfit: ${e.message}" }
            }
        }
    }


    fun deleteOutfit(outfitId: String) {
        if (userId == null) {
            _errorMessage.update { "User not authenticated. Cannot delete outfit." }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                firestore.collection("outfits")
                    .document(userId)
                    .collection("user_outfits")
                    .document(outfitId)
                    .delete()
                    .await()

                Log.d("OutfitsViewModel", "Outfit deleted successfully: $outfitId")
            } catch (e: Exception) {
                Log.e("OutfitsViewModel", "Error deleting outfit: ${e.message}", e)
                _errorMessage.update { "Error deleting outfit: ${e.message}" }
            }
        }
    }
}
