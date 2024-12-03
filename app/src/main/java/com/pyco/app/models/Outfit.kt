package com.pyco.app.models

import com.google.firebase.firestore.DocumentReference

data class Outfit(
    val id: String = "",
    val name: String = "",
    val top: DocumentReference,
    val bottom: DocumentReference,
    val shoe: DocumentReference,
    val accessory: DocumentReference? = null
)

