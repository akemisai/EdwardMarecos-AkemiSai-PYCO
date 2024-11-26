package com.example.roomdemo

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel(application: Application) : ViewModel() {

    val allProducts: LiveData<List<Product>>
    private val repository: ProductRepository
    val searchResults: MutableLiveData<List<Product>>

    init {
        val productDb = ProductRoomDatabase.getInstance(application)
        val productDao = productDb.productDao()
        repository = ProductRepository(productDao)

        allProducts = repository.allProducts
        searchResults = repository.searchResults
    }

    fun insertProduct(product: Product) {
        repository.insertProduct(product)
    }

    fun findProduct(name: String) {
        repository.findProduct(name)
    }

    fun deleteProduct(name: String) {
        repository.deleteProduct(name)
    }
}