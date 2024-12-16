package com.pyco.app.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

data class User(
    val uid: String = "",                                   // User's unique ID
    val email: String = "",                                 // User's email
    val displayName: String = "",                           // User's display name
    val photoURL: String = "",                              // URL to user's profile picture
    val bookmarkedOutfits: List<String> = emptyList(),      // Array of outfit IDs the user bookmarked
    val followers: List<DocumentReference> = emptyList(),   // List of references to users who follow this user
    val following: List<DocumentReference> = emptyList(),   // List of references to users the current user is following
    val likesGiven: List<String> = emptyList(),               // List of likes the user has given (outfits liked by user)

    val followersCount: Int = 0,                            // Count of followers
    val followingCount: Int = 0,                            // Count of following
    val likesCount: Int = 0                                 // Total number of likes given + received
)

data class Like(
    val userId: String = "",                                // User who liked the outfit
    val outfitId: String = "",                              // Outfit that was liked
    val timestamp: Timestamp = Timestamp.now()              // Time when the like was given
)
