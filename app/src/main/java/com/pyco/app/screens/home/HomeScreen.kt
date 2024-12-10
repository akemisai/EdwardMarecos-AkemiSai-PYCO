package com.pyco.app.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pyco.app.components.BottomNavigationBar
import com.pyco.app.components.backgroundColor
import com.pyco.app.screens.home.components.HomeTopSection
import com.pyco.app.viewmodels.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel = viewModel()) {
    val publicOutfits by homeViewModel.publicOutfits.collectAsState()

    Scaffold(
        containerColor = backgroundColor,
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                HomeTopSection()
            }
        }
    }
}