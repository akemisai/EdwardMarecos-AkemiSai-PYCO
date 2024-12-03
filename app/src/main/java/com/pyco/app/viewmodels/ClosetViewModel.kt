package com.pyco.app.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pyco.app.models.ClothingItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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
                    // Handle error
                    return@addSnapshotListener
                }
                val items = snapshot?.toObjects(ClothingItem::class.java).orEmpty()
                // Organize items by type
                _tops.value = items.filter { it.type == "top" }
                _bottoms.value = items.filter { it.type == "bottom" }
                _shoes.value = items.filter { it.type == "shoe" }
                _accessories.value = items.filter { it.type == "accessory" }
            }
    }
    fun addClothingItem(item: ClothingItem) {
        if (userId == null) return

        val wardrobeRef = firestore.collection("users").document(userId).collection("wardrobe")
        val docRef = wardrobeRef.document()
        val itemWithId = item.copy(id = docRef.id)

        docRef.set(itemWithId)
            .addOnSuccessListener {
                // Optionally notify success
            }
            .addOnFailureListener {
                // Handle error (e.g., log it)
            }
    }
}
