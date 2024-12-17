package com.pyco.app.models

import com.google.firebase.Timestamp

data class Request(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val ownerId: String = "",
    val ownerName: String = "",
    val ownerPhotoUrl: String = "",
    val responses: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val timestamp: Timestamp = Timestamp.now()
)
