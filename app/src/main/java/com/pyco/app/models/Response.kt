package com.pyco.app.models

data class Response(
    val id: String = "",
    val requestId: String = "",
    val responderId: String = "",
    val outfitId: String = "",
    val timestamp: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now()
)