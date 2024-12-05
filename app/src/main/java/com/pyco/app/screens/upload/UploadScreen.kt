
package com.pyco.app.screens.upload

import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pyco.app.MainActivity
import com.pyco.app.components.BottomNavigationBar
import com.pyco.app.screens.upload.components.CameraPreview
import com.pyco.app.R
import com.pyco.app.screens.closet.closetBackgroundColor
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun UploadScreen(
    navController: NavHostController
) {
    val context = LocalContext.current as MainActivity
    val permissionGranted = context.isCameraPermissionGranted.collectAsState().value

    val imageCapture = remember { ImageCapture.Builder().build() }
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    LaunchedEffect(Unit) {
        context.handleCameraPermission()
    }

    Scaffold(
        containerColor = closetBackgroundColor, // Set the background color
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (permissionGranted) {
                    CameraPreview(
                        imageCapture = imageCapture,
                        onCaptureClick = {
                            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(File(context.filesDir, "photo.jpg")).build()
                            imageCapture.takePicture(outputFileOptions, cameraExecutor,
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onError(error: ImageCaptureException) {
                                        // Handle the error
                                    }
                                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                        // Handle the saved image
                                    }
                                })
                        }
                    )
                } else {
                    Button(onClick = { context.handleCameraPermission() }) {
                        Text("Request Camera Permission")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_6_pro", name = "fone")
@Composable
fun UploadScreenPreview() {
    UploadScreen(
        navController = NavHostController(LocalContext.current)
    )
}