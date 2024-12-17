package com.pyco.app.models

import com.google.firebase.Timestamp

data class Response(
    val id: String = "",
    val requestId: String = "",
    val responderId: String = "",
    val outfitId: String = "",
    val comment: String = "",
    val timestamp: Timestamp
)