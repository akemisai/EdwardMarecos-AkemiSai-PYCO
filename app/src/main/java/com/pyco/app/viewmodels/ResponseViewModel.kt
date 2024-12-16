package com.pyco.app.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.pyco.app.models.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ResponseViewModel(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _responses = MutableStateFlow<List<Response>>(emptyList())
    val responses: StateFlow<List<Response>> = _responses

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun createResponse(requestId: String, responderId: String, outfitId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val newResponseRef = firestore.collection("responses").document()

                val newResponse = Response(
                    id = newResponseRef.id,
                    requestId = requestId,
                    responderId = responderId,
                    outfitId = outfitId
                )

                firestore.runTransaction { transaction ->
                    transaction.set(newResponseRef, newResponse)
                }.await()

                Log.d("ResponseViewModel", "Response created successfully for request: $requestId")
            } catch (e: Exception) {
                Log.e("ResponseViewModel", "Error creating response: ${e.message}", e)
                _errorMessage.value = "Error creating response: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchResponsesForRequest(requestId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val snapshot = firestore.collection("responses")
                    .whereEqualTo("requestId", requestId)
                    .get()
                    .await()

                val responsesList = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Response::class.java)?.copy(id = doc.id)
                }

                _responses.value = responsesList
                Log.d("ResponseViewModel", "Fetched ${responsesList.size} responses for request: $requestId")
            } catch (e: Exception) {
                Log.e("ResponseViewModel", "Error fetching responses: ${e.message}")
                _errorMessage.value = "Error fetching responses: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}