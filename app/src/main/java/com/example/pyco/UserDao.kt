package com.example.pyco

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {

    @Insert
    fun insertUser(user: User) // No suspend here

    @Update
    fun updateUser(user: User) // No suspend here

    @Query("SELECT * FROM users WHERE user_id = :id")
    fun getUserById(id: Int): User? // Direct return type

    @Query("SELECT * FROM users WHERE username = :username")
    fun getUserByUsername(username: String): User? // Direct return type

    @Query("DELETE FROM users WHERE user_id = :id")
    fun deleteUserById(id: Int): Int // Should return the number of rows affected

    @Query("SELECT * FROM users")
    fun getAllUsers(): LiveData<List<User>> // LiveData for observing changes
}