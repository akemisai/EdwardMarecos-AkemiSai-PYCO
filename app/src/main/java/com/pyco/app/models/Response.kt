package com.pyco.app.models

import com.google.firebase.Timestamp

data class Response(
    val id: String = "",
    val title: String = "",
    val requestId: String = "",
    val responderId: String = "",
    val outfitId: String = "",
    val outfitName: String = "",
    val requestDescription: String = "",
    val comment: String = "",
    val timestamp: Timestamp = Timestamp.now()
)