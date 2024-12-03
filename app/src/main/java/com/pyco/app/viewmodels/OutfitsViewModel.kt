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

                    // Resolve clothing references
                    resolveClothingReferences(outfitsList)
                }
            }
    }

    // Function to resolve DocumentReferences
    private fun resolveClothingReferences(outfits: List<Outfit>) {
        val resolvedOutfits = outfits.map { outfit ->
            val topItem = outfit.top?.get()?.addOnSuccessListener { it.toObject(ClothingItem::class.java) }
            val bottomItem = outfit.bottom?.get()?.addOnSuccessListener { it.toObject(ClothingItem::class.java) }
            val shoeItem = outfit.shoe?.get()?.addOnSuccessListener { it.toObject(ClothingItem::class.java) }
            val accessoryItem = outfit.accessory?.get()?.addOnSuccessListener { it.toObject(ClothingItem::class.java) }

            Log.d("OutfitResolution", "Resolved Outfit: top=$topItem, bottom=$bottomItem, shoe=$shoeItem, accessory=$accessoryItem")
            outfit // You can also add the resolved items to another map for UI use
        }

        _outfits.value = resolvedOutfits
    }


    // Fetch wardrobe items from Firestore and map them
    private fun fetchWardrobe() {
        if (userId == null) return

        firestore.collection("users").document(userId).collection("wardrobe")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("OutfitsViewModel", "Error fetching wardrobe items", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val map = snapshot.documents.mapNotNull { doc ->
                        val clothingItem = doc.toObject(ClothingItem::class.java)
                        if (clothingItem != null) {
                            clothingItem.id to clothingItem
                        } else {
                            null
                        }
                    }.toMap()
                    _wardrobeMap.value = map
                }
            }
    }

    // Function to add an outfit
    fun addOutfit(
        name: String,
        topRef: DocumentReference?,
        bottomRef: DocumentReference?,
        shoeRef: DocumentReference?,
        accessoryRef: DocumentReference?
    ) {
        if (userId == null || topRef == null || bottomRef == null || shoeRef == null || accessoryRef == null) {
            Log.e("OutfitsViewModel", "Missing references or user ID")
            return
        }

        val outfit = Outfit(
            name = name,
            top = topRef,
            bottom = bottomRef,
            shoe = shoeRef,
            accessory = accessoryRef
        )

        val outfitsRef = firestore.collection("users").document(userId).collection("outfits")
        val docRef = outfitsRef.document()
        val outfitWithId = outfit.copy(id = docRef.id)

        docRef.set(outfitWithId)
            .addOnSuccessListener {
                Log.d("OutfitsViewModel", "Outfit added successfully: $outfitWithId")
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
