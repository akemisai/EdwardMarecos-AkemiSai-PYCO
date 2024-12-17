package com.pyco.app.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

data class Outfit(
    val id: String = "",                            // Unique ID for the outfit
    val name: String = "",                          // Name of the outfit
    val top: DocumentReference? = null,             // References to clothing items
    val bottom: DocumentReference? = null,          // References to clothing items
    val shoe: DocumentReference? = null,            // References to clothing items
    val accessory: DocumentReference? = null,       // References to clothing items
    val createdBy: String = "",                     // User who created the outfit
    val creatorId: String = "",                     // User ID of the creator
    val public: Boolean = false,                    // Whether the outfit is public
    val creatorPhotoUrl: String = "",               // URL of the creator's profile picture
    val ownerId: String = "",                       // User ID of the owner of the outfit
    val likes: List<String> = emptyList(),          // List of user IDs who liked the outfit
    val tags: List<String> = emptyList(),           // List of tags associated with the outfit
    val timestamp: Timestamp = Timestamp.now()      // Timestamp when the outfit was created
)
