package com.pyco.app.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.pyco.app.models.Outfit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _publicOutfits = MutableStateFlow<List<Outfit>>(emptyList())
    val publicOutfits: StateFlow<List<Outfit>> = _publicOutfits.asStateFlow()

    init {
        fetchPublicOutfits()
    }

    private fun fetchPublicOutfits() {
        firestore.collection("public_outfits")
            .get()
            .addOnSuccessListener { snapshot ->
                val outfits = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Outfit::class.java)?.copy(id = doc.id)
                }
                _publicOutfits.value = outfits
                Log.d("HomeViewModel", "Fetched public outfits: ${_publicOutfits.value}")
            }
            .addOnFailureListener { error ->
                Log.e("HomeViewModel", "Error fetching public outfits", error)
            }
    }
}
