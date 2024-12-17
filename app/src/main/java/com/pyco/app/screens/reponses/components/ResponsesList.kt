package com.pyco.app.screens.responses.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.pyco.app.models.Response
import com.pyco.app.screens.reponses.components.ResponseCard
import com.pyco.app.viewmodels.ResponseViewModel

@Composable
fun ResponsesList(requestId: String, responsesViewModel: ResponseViewModel = viewModel()) {
    val responses by responsesViewModel.responses.collectAsState()

    LaunchedEffect(requestId) {
        responsesViewModel.fetchResponsesForRequest(requestId)
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(responses) { response ->
            ResponseCard(response = response)
        }
    }
}