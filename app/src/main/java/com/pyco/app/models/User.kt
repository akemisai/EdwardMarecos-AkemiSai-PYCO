package com.pyco.app.models

data class User(
    val uid: String = "",                                   // User's unique ID
    val email: String = "",                                 // User's email
    val displayName: String = "",                           // User's display name
    val photoURL: String = "",                              // URL to user's profile picture
    val bookmarkedOutfits: List<String> = emptyList(),      // Array of outfit IDs the user bookmarked
    val followers: List<String> = emptyList(),              // List of users who follow this user
    val following: List<String> = emptyList(),              // List of users the current user is following
    val likesGiven: List<String> = emptyList(),             // List of likes the user has given (outfits liked by user)

    val followersCount: Int = 0,                            // Count of followers
    val followingCount: Int = 0,                            // Count of following
    val likesCount: Int = 0                                 // Total number of likes received
)
