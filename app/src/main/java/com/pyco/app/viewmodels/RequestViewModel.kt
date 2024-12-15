package com.pyco.app.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.pyco.app.models.Request
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RequestViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    // MutableStateFlow for requests
    private val _requests = MutableStateFlow<List<Request>>(emptyList())
    val requests: StateFlow<List<Request>> = _requests.asStateFlow()

    init {
        fetchRequests()
    }

    // Function to fetch all requests
    private fun fetchRequests() {
        firestore.collection("requests")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Error fetching requests: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val requestList = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Request::class.java)
                    }
                    _requests.value = requestList
                }
            }
    }

    // Function to create a new request
    fun createRequest(userId: String, wardrobeId: String, color: String? = null, material: String? = null) {
        val wardrobeRef: DocumentReference = firestore.collection("wardrobes").document(wardrobeId)

        val request = Request(
            userId = userId,
            wardrobe = wardrobeRef,
            color = color,
            material = material
        )

        // Add request to the "requests" collection
        firestore.collection("requests")
            .add(request)
            .addOnSuccessListener {
                // Handle success
                println("Request created successfully!")
            }
            .addOnFailureListener { e ->
                // Handle failure
                println("Error creating request: ${e.message}")
            }
    }
}
