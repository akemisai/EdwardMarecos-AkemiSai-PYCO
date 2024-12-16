package com.pyco.app.models

data class Request(
    val id: String = "",
    val description: String = "",
    val ownerId: String = "",
    val ownerName: String = "",
    val ownerPhotoUrl: String = "",
    val responses: List<String> = emptyList(),
    val timestamp: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now()
)
