package com.example.pyco

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    val userId: Int = 0,

    @ColumnInfo(name = "username")
    var username: String,

    @ColumnInfo(name = "email")
    var email: String,

    @ColumnInfo(name = "profile_picture")
    var profilePicture: String? = null, // Nullable, might not be available initially

    @ColumnInfo(name = "bio")
    var bio: String = "",

    @ColumnInfo(name = "follower_count")
    var followerCount: Int = 0,

    @ColumnInfo(name = "following_count")
    var followingCount: Int = 0,

    @ColumnInfo(name = "total_likes")
    var totalLikes: Int = 0,

    @ColumnInfo(name = "total_bookmarks")
    var totalBookmarks: Int = 0,

    @ColumnInfo(name = "total_awards")
    var totalAwards: Int = 0,

    @ColumnInfo(name = "date_joined")
    val dateJoined: Date = Date() // Default to the current timestamp
)