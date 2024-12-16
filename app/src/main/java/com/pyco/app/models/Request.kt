package com.pyco.app.models

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.Timestamp

data class Request(
    val userId: String = "", // ID of the user making the request
    val wardrobe: DocumentReference? = null, // Reference to the user's wardrobe
    val color: String? = null, // Optional color preference
    val material: String? = null, // Optional material preference
    val timestamp: Timestamp = Timestamp.now() // When the request was made
)
