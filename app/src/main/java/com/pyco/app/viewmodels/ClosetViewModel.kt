package com.pyco.app.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pyco.app.models.ClothingItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClosetViewModel(
    private val userViewModel: UserViewModel // Inject UserViewModel
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

    init {
        viewModelScope.launch {
            userViewModel.userProfile.collect { user ->
                user?.wardrobeId?.let { wardrobeId ->
                    fetchClothingItems(wardrobeId)
                } ?: Log.e("ClosetViewModel", "Wardrobe ID is null for user.")
            }
        }
    }

    private fun fetchClothingItems(wardrobeId: String) {
        Log.d("ClosetViewModel", "Fetching items for wardrobe: $wardrobeId")

        // Define categories and their respective flows
        val categories = mapOf(
            "tops" to _tops,
            "bottoms" to _bottoms,
            "shoes" to _shoes,
            "accessories" to _accessories
        )

        categories.forEach { (category, flow) ->
            Log.d("ClosetViewModel", "Fetching items from category: $category")

            firestore.collection("wardrobes")
                .document(wardrobeId)
                .collection(category)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("ClosetViewModel", "Error fetching $category items: ${error.message}")
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        Log.d("ClosetViewModel", "Fetched ${snapshot.size()} items from $category")
                        val items = snapshot.documents.mapNotNull { doc ->
                            try {
                                doc.toObject(ClothingItem::class.java)?.copy(id = doc.id)
                            } catch (e: Exception) {
                                Log.e("ClosetViewModel", "Error deserializing document ${doc.id} in $category", e)
                                null
                            }
                        }
                        flow.value = items
                    } else {
                        Log.e("ClosetViewModel", "Snapshot is null for category: $category")
                    }
                }
        }
    }

    fun addClothingItem(item: ClothingItem, wardrobeId: String, category: String) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e("ClosetViewModel", "User not authenticated. Cannot add item.")
            return
        }

        val validCategories = listOf("tops", "bottoms", "shoes", "accessories")
        if (!validCategories.contains(category)) {
            Log.e("ClosetViewModel", "Invalid category: $category")
            return
        }

        val categoryRef = firestore.collection("wardrobes")
            .document(wardrobeId)
            .collection(category)

        val docRef = categoryRef.document()
        val itemWithId = item.copy(id = docRef.id)

        firestore.runTransaction { transaction ->
            transaction.set(docRef, itemWithId)
        }.addOnSuccessListener {
            Log.d("ClosetViewModel", "ClothingItem added successfully to $category: $itemWithId")
        }.addOnFailureListener { exception ->
            Log.e("ClosetViewModel", "Error adding ClothingItem to $category", exception)
        }
    }
}

//    // Function to add a clothing item with a custom ID
//    fun addClothingItemWithCustomId(item: ClothingItem, customId: String) {
//        if (userId == null) {
//            Log.e("ClosetViewModel", "User not authenticated")
//            return
//        }
//
//        val wardrobeRef = firestore.collection("users").document(userId).collection("wardrobe")
//        val docRef = wardrobeRef.document(customId) // Use custom ID
//
//        val itemWithId = item.copy(id = customId)
//
//        firestore.runTransaction { transaction ->
//            transaction.set(docRef, itemWithId)
//        }.addOnSuccessListener {
//            Log.d("ClosetViewModel", "ClothingItem added successfully with custom ID")
//        }.addOnFailureListener { exception ->
//            Log.e("ClosetViewModel", "Error adding ClothingItem", exception)
//        }
//    }