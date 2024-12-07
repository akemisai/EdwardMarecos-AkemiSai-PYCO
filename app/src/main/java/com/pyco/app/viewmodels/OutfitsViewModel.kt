package com.pyco.app.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentReference
import com.pyco.app.models.ClothingItem
import com.pyco.app.models.Outfit
import com.pyco.app.screens.outfits.creation.components.PublicOutfitCreator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OutfitsViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userId = auth.currentUser?.uid

    // StateFlows for outfits and wardrobe map
    private val _outfits = MutableStateFlow<List<Outfit>>(emptyList())
    val outfits: StateFlow<List<Outfit>> = _outfits.asStateFlow()

    private val _wardrobeMap = MutableStateFlow<Map<String, ClothingItem>>(emptyMap())
    val wardrobeMap: StateFlow<Map<String, ClothingItem>> = _wardrobeMap.asStateFlow()

    init {
        fetchWardrobeItems()
        fetchUserOutfits()
    }

    // Fetch wardrobe items from Firestore
    private fun fetchWardrobeItems() {
        if (userId == null) {
            Log.e("OutfitsViewModel", "User not authenticated. Cannot fetch wardrobe items.")
            return
        }

        val categories = listOf("tops", "bottoms", "shoes", "accessories")

        categories.forEach { category ->
            firestore.collection("wardrobes")
                .document(userId) // Use `userId` to identify the wardrobe
                .collection(category) // Fetch items from the specific category
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("OutfitsViewModel", "Error fetching $category items: ${error.message}")
                        return@addSnapshotListener
                    }

                    snapshot?.let {
                        val wardrobeItems = it.documents.mapNotNull { doc ->
                            doc.toObject(ClothingItem::class.java)?.copy(id = doc.id)
                        }.associateBy { it.id }
                        _wardrobeMap.value += wardrobeItems
                        Log.d("OutfitsViewModel", "Wardrobe items fetched for $category: $wardrobeItems")
                    }
                }
        }
    }

    // Fetch user outfits from Firestore
    private fun fetchUserOutfits() {
        if (userId == null) {
            Log.e("OutfitsViewModel", "User not authenticated. Cannot fetch outfits.")
            return
        }

        firestore.collection("outfits").document(userId)
            .collection("user_outfits") // Assuming "user_outfits" as a subcollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("OutfitsViewModel", "Error fetching outfits: ${error.message}")
                    return@addSnapshotListener
                }

                snapshot?.let {
                    val outfitsList = it.documents.mapNotNull { doc ->
                        doc.toObject(Outfit::class.java)?.copy(id = doc.id)
                    }
                    _outfits.value = outfitsList
                    Log.d("OutfitsViewModel", "User outfits fetched: $outfitsList")
                }
            }
    }

    // Add an outfit to Firestore
    fun addOutfit(
        name: String,
        topRef: DocumentReference,
        bottomRef: DocumentReference,
        shoeRef: DocumentReference,
        accessoryRef: DocumentReference?, // Optional
        isPublic: Boolean
    ) {
        if (userId == null) {
            Log.e("OutfitsViewModel", "User not authenticated. Cannot add outfit.")
            return
        }

        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val displayName = document.getString("displayName") ?: "Unknown User"

                val newOutfit = Outfit(
                    id = "", // Firestore will generate this
                    name = name,
                    createdBy = displayName,
                    top = topRef,
                    bottom = bottomRef,
                    shoe = shoeRef,
                    accessory = accessoryRef,
                    isPublic = isPublic
                )

                // Add to user outfits collection
                val outfitsRef = firestore.collection("outfits").document(userId).collection("user_outfits")
                val newDocRef = outfitsRef.document()

                newDocRef.set(newOutfit.copy(id = newDocRef.id))
                    .addOnSuccessListener {
                        Log.d("OutfitsViewModel", "Outfit added successfully.")

                        // If public, add to public feed
                        if (isPublic) {
                            val publicOutfitCreator = PublicOutfitCreator()
                            publicOutfitCreator.createPublicOutfit(
                                newOutfit.copy(id = newDocRef.id),
                                onSuccess = {
                                    Log.d("OutfitsViewModel", "Public outfit creation succeeded.")
                                },
                                onFailure = { error ->
                                    Log.e("OutfitsViewModel", "Public outfit creation failed: ${error.message}")
                                }
                            )
                        }

                    }
                    .addOnFailureListener { error ->
                        Log.e("OutfitsViewModel", "Error adding outfit: ${error.message}")
                    }
            }
            .addOnFailureListener { error ->
                Log.e("OutfitsViewModel", "Error fetching user displayName: ${error.message}")
            }
    }

    // Delete an outfit
    fun deleteOutfit(outfitId: String) {
        if (userId == null) {
            Log.e("OutfitsViewModel", "User not authenticated. Cannot delete outfit.")
            return
        }

        firestore.collection("outfits").document(userId).collection("user_outfits")
            .document(outfitId)
            .delete()
            .addOnSuccessListener {
                Log.d("OutfitsViewModel", "Outfit deleted successfully.")
                firestore.collection("public_outfits").document(outfitId).delete()
                    .addOnSuccessListener {
                        Log.d("OutfitsViewModel", "Outfit removed from public feed.")
                    }
                    .addOnFailureListener { error ->
                        Log.e("OutfitsViewModel", "Error removing outfit from public feed: ${error.message}")
                    }
            }
            .addOnFailureListener { error ->
                Log.e("OutfitsViewModel", "Error deleting outfit: ${error.message}")
            }
    }
}
