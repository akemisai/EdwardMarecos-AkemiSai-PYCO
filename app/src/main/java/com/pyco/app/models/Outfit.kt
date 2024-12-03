package com.pyco.app.models

import com.google.firebase.firestore.DocumentReference

data class Outfit(
    val id: String = "",
    val name: String = "",
    val top: DocumentReference? = null,
    val bottom: DocumentReference? = null,
    val shoe: DocumentReference? = null,
    val accessory: DocumentReference? = null,
    val createdBy: String = "", // User-inputted name until usernames are implemented
    val isPublic: Boolean = false // Field to indicate if the outfit is public
)
