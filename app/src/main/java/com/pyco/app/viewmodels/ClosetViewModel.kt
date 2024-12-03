package com.pyco.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.pyco.app.models.ClothingItem
import com.pyco.app.models.toMap

class ClosetViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _wardrobe = MutableLiveData<List<ClothingItem>>()
    val wardrobe: LiveData<List<ClothingItem>> = _wardrobe

    // Add ClothingItem to Firestore
    fun addClothingItem(userId: String, clothingItem: ClothingItem, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        db.collection("users")
            .document(userId)
            .collection("wardrobe")
            .add(clothingItem.toMap())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    // Fetch Wardrobe from Firestore
    fun fetchWardrobe(userId: String, onError: ((Exception) -> Unit)? = null) {
        db.collection("users")
            .document(userId)
            .collection("wardrobe")
            .get()
            .addOnSuccessListener { result ->
                val wardrobeList = result.documents.mapNotNull { document ->
                    document.toObject(ClothingItem::class.java)
                }
                _wardrobe.value = wardrobeList
            }
            .addOnFailureListener { exception ->
                onError?.invoke(exception)
            }
    }
}