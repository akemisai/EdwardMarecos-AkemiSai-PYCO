package com.pyco.app.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pyco.app.models.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RequestViewModel (
    val userViewModel: UserViewModel,
    val firestore: FirebaseFirestore
) : ViewModel() {

    private val _requests = MutableStateFlow<List<Request>>(emptyList())
    val requests: StateFlow<List<Request>> = _requests

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage


    fun createRequest(
        description: String,
        ownerId: String,
        ownerName: String,
        ownerPhotoUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val newRequestRef = firestore.collection("requests").document()

                val newRequest = Request(
                    id = newRequestRef.id,
                    description = description,
                    ownerId = ownerId,
                    ownerName = ownerName,
                    ownerPhotoUrl = ownerPhotoUrl,
                    responses = emptyList(),
                    timestamp = Timestamp.now()
                )

                firestore.runTransaction { transaction ->
                    transaction.set(newRequestRef, newRequest)
                }.await()

                Log.d("RequestsViewModel", "Request created successfully: $description")
            } catch (e: Exception) {
                Log.e("RequestsViewModel", "Error creating request: ${e.message}", e)
                _errorMessage.value = "Error creating request: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    // Fetch all requests from Firestore and update the StateFlow.

    fun fetchRequests() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val snapshot = firestore.collection("requests")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val requestsList = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Request::class.java)?.copy(id = doc.id)
                }

                _requests.value = requestsList
                Log.d("RequestsViewModel", "Fetched ${requestsList.size} requests.")
            } catch (e: Exception) {
                Log.e("RequestsViewModel", "Error fetching requests: ${e.message}")
                _errorMessage.value = "Error fetching requests: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addResponseToRequest(requestId: String, responseId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val requestRef = firestore.collection("requests").document(requestId)
                firestore.runTransaction { transaction ->
                    val snapshot = transaction.get(requestRef)
                    val currentResponses = snapshot.get("responses") as? List<String> ?: emptyList()
                    val updatedResponses = currentResponses + responseId
                    transaction.update(requestRef, "responses", updatedResponses)
                }.await()

                Log.d("RequestsViewModel", "Response $responseId added to request $requestId successfully.")
            } catch (e: Exception) {
                Log.e("RequestsViewModel", "Error adding response to request: ${e.message}", e)
                _errorMessage.value = "Error adding response to request: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}