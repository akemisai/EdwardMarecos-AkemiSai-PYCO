package com.pyco.app.screens.upload

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pyco.app.MainActivity
import com.pyco.app.base.CameraPreview
import com.pyco.app.components.BottomNavigationBar

@Composable
fun UploadScreen(
    navController: NavHostController
) {
    val context = LocalContext.current as MainActivity
    val permissionGranted = context.isCameraPermissionGranted.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (permissionGranted) {
            CameraPreview()
        } else {
            Button(onClick = { context.handleCameraPermission() }) {
                Text("Request Camera Permission")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Use the navigation bar to go to other sections.")
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BottomNavigationBar(navController = navController)
    }
}

@Preview(showBackground = true, device = "id:pixel_6_pro", name = "fone")
@Composable
fun UploadScreenPreview() {
    UploadScreen(
        navController = NavHostController(LocalContext.current)
    )
}
