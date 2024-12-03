package com.pyco.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pyco.app.models.Outfit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OutfitsViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userId = auth.currentUser?.uid

    private val _outfits = MutableStateFlow<List<Outfit>>(emptyList())
    val outfits: StateFlow<List<Outfit>> = _outfits

    init {
        fetchOutfits()
    }

    private fun fetchOutfits() {
        if (userId == null) return

        firestore.collection("users").document(userId).collection("outfits")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error (e.g., log it)
                    return@addSnapshotListener
                }
                val items = snapshot?.toObjects(Outfit::class.java).orEmpty()
                _outfits.value = items
            }
    }

    fun addOutfit(outfit: Outfit) {
        if (userId == null) return

        val outfitsRef = firestore.collection("users").document(userId).collection("outfits")
        val docRef = outfitsRef.document()
        val outfitWithId = outfit.copy(id = docRef.id)

        docRef.set(outfitWithId)
            .addOnSuccessListener {
                // Optionally notify success
            }
            .addOnFailureListener {
                // Handle error (e.g., log it)
            }
    }
}