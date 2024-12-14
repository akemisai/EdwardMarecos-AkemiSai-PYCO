package com.pyco.app.screens.outfits.creation.components

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.pyco.app.models.Outfit

class PublicOutfitCreator {
    private val firestore = FirebaseFirestore.getInstance()

    fun createPublicOutfit(
        originalOutfit: Outfit,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (originalOutfit.id.isBlank()) {
            Log.e("PublicOutfitCreator", "Outfit ID is invalid or blank.")
            onFailure(IllegalArgumentException("Invalid outfit ID"))
            return
        }

        val originalOutfitRef = firestore.collection("outfits")
            .document(originalOutfit.ownerId)
            .collection("user_outfits")
            .document(originalOutfit.id)

        val publicOutfitData = mapOf(
            "id" to originalOutfit.id,
            "name" to originalOutfit.name,
            "top" to originalOutfit.top,
            "bottom" to originalOutfit.bottom,
            "shoe" to originalOutfit.shoe,
            "accessory" to originalOutfit.accessory,
            "createdBy" to originalOutfit.createdBy,
            "creatorId" to originalOutfit.creatorId,
            "creatorPhotoUrl" to originalOutfit.creatorPhotoUrl,
            "ownerId" to originalOutfit.ownerId,
            "timestamp" to originalOutfit.timestamp,
            "likes" to emptyList<String>(),
            "isPublic" to true,
            "originalOutfitRef" to originalOutfitRef
        )

        firestore.collection("public_outfits").document(originalOutfit.id)
            .set(publicOutfitData)
            .addOnSuccessListener {
                Log.d("PublicOutfitCreator", "Outfit reference added to public feed.")
                onSuccess()
            }
            .addOnFailureListener { error ->
                Log.e("PublicOutfitCreator", "Error adding outfit reference to public feed: ${error.message}")
                onFailure(error)
            }
    }
}