package com.pyco.app.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pyco.app.models.ClothingItem
import com.pyco.app.models.ClothingType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.util.Log

class ClosetViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userId = auth.currentUser?.uid

    private val _tops = MutableStateFlow<List<ClothingItem>>(emptyList())
    val tops: StateFlow<List<ClothingItem>> = _tops

    private val _bottoms = MutableStateFlow<List<ClothingItem>>(emptyList())
    val bottoms: StateFlow<List<ClothingItem>> = _bottoms

    private val _shoes = MutableStateFlow<List<ClothingItem>>(emptyList())
    val shoes: StateFlow<List<ClothingItem>> = _shoes

    private val _accessories = MutableStateFlow<List<ClothingItem>>(emptyList())
    val accessories: StateFlow<List<ClothingItem>> = _accessories

    init {
        fetchClothingItems()
    }

    private fun fetchClothingItems() {
        if (userId == null) return

        firestore.collection("users").document(userId).collection("wardrobe")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ClosetViewModel", "Error fetching wardrobe items", error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val items = snapshot.documents.mapNotNull { doc ->
                        try {
                            val clothingItem = doc.toObject(ClothingItem::class.java)
                            if (clothingItem != null) {
                                clothingItem.copy(id = doc.id) // Populate ID from Firestore document
                            } else {
                                Log.w("ClosetViewModel", "Document ${doc.id} is null")
                                null
                            }
                        } catch (e: Exception) {
                            Log.e("ClosetViewModel", "Error deserializing document ${doc.id}", e)
                            null
                        }
                    }

                    // Organize items by type
                    _tops.value = items.filter { it.type == ClothingType.TOP }
                    _bottoms.value = items.filter { it.type == ClothingType.BOTTOM }
                    _shoes.value = items.filter { it.type == ClothingType.SHOE }
                    _accessories.value = items.filter { it.type == ClothingType.ACCESSORY }
                }
            }
    }

    // Function to add a clothing item with a custom ID
    fun addClothingItemWithCustomId(item: ClothingItem, customId: String) {
        if (userId == null) {
            Log.e("ClosetViewModel", "User not authenticated")
            return
        }

        val wardrobeRef = firestore.collection("users").document(userId).collection("wardrobe")
        val docRef = wardrobeRef.document(customId) // Use custom ID

        val itemWithId = item.copy(id = customId)

        firestore.runTransaction { transaction ->
            transaction.set(docRef, itemWithId)
        }.addOnSuccessListener {
            Log.d("ClosetViewModel", "ClothingItem added successfully with custom ID")
        }.addOnFailureListener { exception ->
            Log.e("ClosetViewModel", "Error adding ClothingItem", exception)
        }
    }

    // Function to add a clothing item with an optional custom ID
    fun addClothingItem(item: ClothingItem) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e("ClosetViewModel", "User not authenticated. Cannot add item.")
            return
        }

        val wardrobeRef = firestore.collection("users").document(userId).collection("wardrobe")
        val docRef = wardrobeRef.document()

        val itemWithId = item.copy(id = docRef.id)

        firestore.runTransaction { transaction ->
            transaction.set(docRef, itemWithId)
        }.addOnSuccessListener {
            Log.d("ClosetViewModel", "ClothingItem added successfully: $itemWithId")
        }.addOnFailureListener { exception ->
            Log.e("ClosetViewModel", "Error adding ClothingItem", exception)
        }
    }
}
