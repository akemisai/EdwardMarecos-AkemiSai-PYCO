package com.pyco.app.models

data class Wardrobe(
    val wardrobeId: String = "",            // Unique ID for the wardrobe
    val userId: String = "",                // Reference to user who owns this wardrobe
)
