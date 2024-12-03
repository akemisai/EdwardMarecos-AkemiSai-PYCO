package com.pyco.app.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pyco.app.models.ClothingItem
import com.pyco.app.models.Outfit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.util.Log
import com.google.firebase.firestore.DocumentReference

class OutfitsViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userId = auth.currentUser?.uid

    // StateFlow for outfits
    private val _outfits = MutableStateFlow<List<Outfit>>(emptyList())
    val outfits: StateFlow<List<Outfit>> = _outfits.asStateFlow()

    // StateFlow for wardrobeMap
    private val _wardrobeMap = MutableStateFlow<Map<String, ClothingItem>>(emptyMap())
    val wardrobeMap: StateFlow<Map<String, ClothingItem>> = _wardrobeMap.asStateFlow()

    init {
        fetchOutfits()
        fetchWardrobe()
    }

    // Fetch outfits from Firestore
    private fun fetchOutfits() {
        if (userId == null) return

        firestore.collection("users").document(userId).collection("outfits")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("OutfitsViewModel", "Error fetching outfits", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val outfitsList = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Outfit::class.java)?.copy(id = doc.id)
                    }
                    Log.d("OutfitsViewModel", "Fetched outfits: $outfitsList")
                    _outfits.value = outfitsList
                }
            }
    }

    // Fetch wardrobe items from Firestore and map them
    private fun fetchWardrobe() {
        if (userId == null) return
        firestore.collection("users").document(userId).collection("wardrobe")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("OutfitsViewModel", "Error fetching wardrobe", error)
                    return@addSnapshotListener
                }

                snapshot?.let {
                    _wardrobeMap.value = it.documents.mapNotNull { doc ->
                        val item = doc.toObject(ClothingItem::class.java)
                        item?.copy(id = doc.id)?.let { it.id to it }
                    }.toMap()
                }
            }
    }


    // Function to add an outfit
    fun addOutfit(
        name: String,
        topRef: DocumentReference,
        bottomRef: DocumentReference,
        shoeRef: DocumentReference,
        accessoryRef: DocumentReference? // Allow null for optional fields
    ) {
        if (userId == null) {
            Log.e("OutfitsViewModel", "User not authenticated")
            return
        }

        val outfitsRef = firestore.collection("users").document(userId).collection("outfits")
        val docRef = outfitsRef.document() // Create new document
        val outfit = Outfit(
            id = docRef.id,
            name = name,
            top = topRef,
            bottom = bottomRef,
            shoe = shoeRef,
            accessory = accessoryRef
        )

        docRef.set(outfit)
            .addOnSuccessListener {
                Log.d("OutfitsViewModel", "Outfit added successfully: $outfit")
            }
            .addOnFailureListener { exception ->
                Log.e("OutfitsViewModel", "Error adding outfit", exception)
            }
    }

    // Optional: Function to delete an outfit
    fun deleteOutfit(outfitId: String) {
        if (userId == null) return

        firestore.collection("users").document(userId).collection("outfits")
            .document(outfitId)
            .delete()
            .addOnSuccessListener {
                Log.d("OutfitsViewModel", "Outfit deleted successfully")
            }
            .addOnFailureListener { exception ->
                Log.e("OutfitsViewModel", "Error deleting outfit", exception)
            }
    }
}
