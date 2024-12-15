
package com.pyco.app.screens.upload

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.pyco.app.MainActivity
import com.pyco.app.components.BottomNavigationBar
import com.pyco.app.screens.upload.components.CameraPreview
import com.pyco.app.R
import com.pyco.app.components.backgroundColor
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@Composable
fun UploadScreen(
    navController: NavHostController
) {
    val context = LocalContext.current as MainActivity
    val permissionGranted = context.isCameraPermissionGranted.collectAsState().value
    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )
    var cameraLaunched by remember { mutableStateOf(false) }

    val cameraLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { success ->
            if (success) {
                Log.d("UploadScreen", "Image captured successfully: $uri")
                navController.navigate("add_wardrobe_item?imageUri=${uri}")
            } else {
                Toast.makeText(context, "Failed to capture image", Toast.LENGTH_SHORT).show()
            }
            cameraLaunched = false
        }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show()
            cameraLauncher.launch(uri)
            cameraLaunched = true
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(permissionGranted) {
        if (permissionGranted && !cameraLaunched) {
            cameraLauncher.launch(uri)
            cameraLaunched = true
        } else if (!permissionGranted) {
            permissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }
}

fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US).format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        imageFileName,
        ".jpg",
        storageDir
    )
}