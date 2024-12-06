package com.pyco.app.models

import com.google.firebase.firestore.DocumentReference

data class Outfit(
    val id: String = "",                        // Unique ID for the outfit
    val name: String = "",                      // Name of the outfit
    val top: DocumentReference? = null,         // References to clothing items
    val bottom: DocumentReference? = null,      // References to clothing items
    val shoe: DocumentReference? = null,        // References to clothing items
    val accessory: DocumentReference? = null,   // References to clothing items
    val createdBy: String = "",                 // User who created the outfit
    val isPublic: Boolean = false               // Whether the outfit is public
)
