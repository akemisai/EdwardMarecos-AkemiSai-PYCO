package com.pyco.app.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pyco.app.models.ClothingItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ClosetViewModel(
    internal val userViewModel: UserViewModel
) : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // StateFlows for clothing categories
    private val _tops = MutableStateFlow<List<ClothingItem>>(emptyList())
    val tops: StateFlow<List<ClothingItem>> = _tops

    private val _bottoms = MutableStateFlow<List<ClothingItem>>(emptyList())
    val bottoms: StateFlow<List<ClothingItem>> = _bottoms

    private val _shoes = MutableStateFlow<List<ClothingItem>>(emptyList())
    val shoes: StateFlow<List<ClothingItem>> = _shoes

    private val _accessories = MutableStateFlow<List<ClothingItem>>(emptyList())
    val accessories: StateFlow<List<ClothingItem>> = _accessories

    // Centralized wardrobe map (key = clothing item ID, value = ClothingItem)
    private val _wardrobeMap = MutableStateFlow<Map<String, ClothingItem>>(emptyMap())
    val wardrobeMap: StateFlow<Map<String, ClothingItem>> = _wardrobeMap

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        // Observe userProfile and fetch data when user is authenticated
        viewModelScope.launch {
            userViewModel.userProfile.collect { user ->
                user?.uid?.let { userId ->
                    fetchClothingItems(userId)
                }
            }
        }
    }

    private suspend fun fetchClothingItems(userId: String) {
        Log.d("ClosetViewModel", "Fetching items for user: $userId")

        // Define categories and their respective flows
        val categories = mapOf(
            "tops" to _tops,
            "bottoms" to _bottoms,
            "shoes" to _shoes,
            "accessories" to _accessories
        )

        _isLoading.value = true

        try {
            categories.forEach { (category, flow) ->
                val snapshot = firestore.collection("wardrobes")
                    .document(userId)
                    .collection(category)
                    .get()
                    .await()

                val items = snapshot.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(ClothingItem::class.java)?.copy(id = doc.id)
                    } catch (e: Exception) {
                        Log.e("ClosetViewModel", "Error deserializing document ${doc.id} in $category", e)
                        null
                    }
                }

                // Update the specific category flow (like _tops, _bottoms)
                flow.value = items

                // Update the wardrobeMap with items from this category
                _wardrobeMap.value += items.associateBy { it.id }
            }
        } catch (e: Exception) {
            Log.e("ClosetViewModel", "Error fetching clothing items: ${e.message}", e)
        } finally {
            _isLoading.value = false
        }
    }

    fun addClothingItem(item: ClothingItem, userId: String, category: String) {
        if (userId.isBlank()) {
            Log.e("ClosetViewModel", "User ID is blank. Cannot add item.")
            return
        }

        if (category !in listOf("tops", "bottoms", "shoes", "accessories")) {
            Log.e("ClosetViewModel", "Invalid category: $category")
            return
        }

        val categoryRef = firestore.collection("wardrobes")
            .document(userId)
            .collection(category)

        val docRef = categoryRef.document()
        val itemWithId = item.copy(id = docRef.id)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                docRef.set(itemWithId).await()
                Log.d("ClosetViewModel", "ClothingItem added successfully to $category: $itemWithId")

                // Update the appropriate category StateFlow directly
                when (category) {
                    "tops" -> _tops.update { it + itemWithId }
                    "bottoms" -> _bottoms.update { it + itemWithId }
                    "shoes" -> _shoes.update { it + itemWithId }
                    "accessories" -> _accessories.update { it + itemWithId }
                }

                // Update the wardrobeMap with the new item
                _wardrobeMap.update { currentMap ->
                    currentMap + (itemWithId.id to itemWithId)
                }
            } catch (e: Exception) {
                Log.e("ClosetViewModel", "Error adding ClothingItem to $category", e)
            }
        }
    }
}