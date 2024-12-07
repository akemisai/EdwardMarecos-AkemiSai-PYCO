package com.pyco.app.screens.outfits.creation.components

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.pyco.app.models.Outfit

class PublicOutfitCreator {
    private val firestore = FirebaseFirestore.getInstance()

    fun createPublicOutfit(outfit: Outfit, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val outfitData = mapOf(
            "name" to outfit.name,
            "createdBy" to outfit.createdBy,
            "top" to outfit.top,
            "bottom" to outfit.bottom,
            "shoe" to outfit.shoe,
            "accessory" to outfit.accessory,
            "isPublic" to outfit.isPublic
        )

        firestore.collection("public_outfits").document(outfit.id)
            .set(outfitData)
            .addOnSuccessListener {
                Log.d("PublicOutfitCreator", "Outfit added to public feed: $outfitData")
                onSuccess()
            }
            .addOnFailureListener { error ->
                Log.e(
                    "PublicOutfitCreator",
                    "Error adding outfit to public feed: ${error.message} and Attempting to write to public_outfits: $outfitData"
                )
                onFailure(error)
            }
    }
}