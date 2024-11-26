package com.example.pyco

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : ViewModel() {

    val allUsers: LiveData<List<User>>
    private val repository: UserRepository
    val searchResults: MutableLiveData<List<User>> = MutableLiveData()

    init {
        val userDb = UserRoomDatabase.getInstance(application)
        val userDao = userDb.userDao()
        repository = UserRepository(userDao)

        allUsers = repository.allUsers
    }

    fun insertUser(user: User) {
        viewModelScope.launch {
            repository.insertUser(user)
        }
    }

    fun findUser(username: String) {
        viewModelScope.launch {
            val results = repository.getUserByUsername(username)
            searchResults.value = results?.let { listOf(it) } ?: emptyList()
        }
    }

    fun deleteUser(username: String) {
        viewModelScope.launch {
            val user = repository.getUserByUsername(username)
            user?.let {
                repository.deleteUserById(it.userId)
            }
        }
    }
}