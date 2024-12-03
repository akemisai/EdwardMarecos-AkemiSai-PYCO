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
        createdBy: String,
        topRef: DocumentReference,
        bottomRef: DocumentReference,
        shoeRef: DocumentReference,
        accessoryRef: DocumentReference?, // Allow null for optional fields
        isPublic: Boolean // Use this value to indicate if the outfit should be public
    ) {
        if (userId == null) {
            Log.e("OutfitsViewModel", "User not authenticated")
            return
        }

        val outfit = Outfit(
            id = "", // Firestore will generate this
            name = name,
            createdBy = createdBy,
            top = topRef,
            bottom = bottomRef,
            shoe = shoeRef,
            accessory = accessoryRef,
            isPublic = isPublic
        )

        // Add to the user's collection
        val userOutfitsRef = firestore.collection("users").document(userId).collection("outfits")
        val newDocRef = userOutfitsRef.document()

        newDocRef.set(outfit.copy(id = newDocRef.id))
            .addOnSuccessListener {
                Log.d("OutfitsViewModel", "Outfit added successfully to user's collection")

                // Add to public feed if it's public
                if (isPublic) {
                    val publicOutfitsRef = firestore.collection("public_outfits")
                    publicOutfitsRef.document(newDocRef.id).set(outfit.copy(id = newDocRef.id))
                        .addOnSuccessListener {
                            Log.d("OutfitsViewModel", "Outfit added successfully to public feed")
                        }
                        .addOnFailureListener { error ->
                            Log.e("OutfitsViewModel", "Error adding outfit to public feed", error)
                        }
                }
            }
            .addOnFailureListener { error ->
                Log.e("OutfitsViewModel", "Error adding outfit to user's collection", error)
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

                // Delete from public feed if it exists there
                firestore.collection("public_outfits").document(outfitId).delete()
                    .addOnSuccessListener {
                        Log.d("OutfitsViewModel", "Outfit removed from public feed")
                    }
                    .addOnFailureListener { error ->
                        Log.e("OutfitsViewModel", "Error removing outfit from public feed", error)
                    }
            }
            .addOnFailureListener { error ->
                Log.e("OutfitsViewModel", "Error deleting outfit", error)
            }
    }
}

