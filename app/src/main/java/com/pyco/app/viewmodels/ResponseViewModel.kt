package com.pyco.app.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.pyco.app.models.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.lifecycle.ViewModelProvider

class ResponseViewModelFactory(
    private val firestore: FirebaseFirestore,
    private val requestViewModel: RequestViewModel,
    private val userViewModel: UserViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResponseViewModel::class.java)) {
            return ResponseViewModel(firestore, requestViewModel, userViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class ResponseViewModel(
    private val firestore: FirebaseFirestore,
    private val requestViewModel: RequestViewModel,
    private val userViewModel: UserViewModel
) : ViewModel() {

    private val _responses = MutableStateFlow<List<Response>>(emptyList())
    val responses: StateFlow<List<Response>> = _responses

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun createResponse(requestId: String, responderId: String, outfitId: String, comment: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val newResponseRef = firestore.collection("responses").document()

                val newResponse = Response(
                    id = newResponseRef.id,
                    requestId = requestId,
                    responderId = responderId,
                    outfitId = outfitId,
                    comment = comment
                )

                firestore.runTransaction { transaction ->
                    // Add the new response
                    transaction.set(newResponseRef, newResponse)

                    // Update the request's responses list
                    val requestRef = firestore.collection("requests").document(requestId)
                    transaction.update(requestRef, "responses", FieldValue.arrayUnion(outfitId))
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
                    doc.toObject(Response::class.java)
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