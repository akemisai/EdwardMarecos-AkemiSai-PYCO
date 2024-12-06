package com.pyco.app.models

data class User(
    val uid: String = "",                               // User's unique ID
    val email: String = "",                             // User's email
    val displayName: String = "",                       // User's display name
    val photoURL: String = "",                          // URL to user's profile picture
    val wardrobeId: String = "",                        // Reference to user's wardrobe document ID
    val likedOutfits: List<String> = emptyList(),       // Array of outfit IDs the user likes
    val bookmarkedOutfits: List<String> = emptyList()   // Array of outfit IDs the user bookmarked
)
