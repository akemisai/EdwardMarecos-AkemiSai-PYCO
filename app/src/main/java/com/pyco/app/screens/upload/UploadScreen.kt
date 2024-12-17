package com.pyco.app.screens.upload

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.pyco.app.components.backgroundColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.pyco.app.MainActivity
import com.pyco.app.components.customColor
import com.pyco.app.screens.upload.components.CameraUI
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(
    navController: NavHostController
) {
    val context = LocalContext.current as MainActivity
    val permissionGranted = context.isCameraPermissionGranted.collectAsState().value

    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var cameraLaunched by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show()
            cameraLaunched = true
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(permissionGranted) {
        if (permissionGranted && !cameraLaunched) {
            cameraLaunched = true
        } else if (!permissionGranted) {
            permissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor,
                    titleContentColor = customColor,
                    navigationIconContentColor = customColor
                ),
            )
        },
        containerColor = backgroundColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (cameraLaunched) {
                    CameraUI(
                        onImageCaptured = { uri ->
                            capturedImageUri = uri
                            cameraLaunched = false
                        },
                        onError = { exception ->
                            Toast.makeText(context, "Failed to capture image: ${exception.message}", Toast.LENGTH_SHORT).show()
                            cameraLaunched = false
                        }
                    )
                } else {
                    if (capturedImageUri?.path?.isNotEmpty() == true) {
                        Image(
                            painter = rememberAsyncImagePainter(capturedImageUri),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(vertical = 8.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .fillMaxWidth()
                                .height(635.dp)
                                .background(Color.Black)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            IconButton(onClick = {
                                removeBackgroundAndUpload(context, capturedImageUri!!) { processedUri ->
                                    navController.navigate("add_wardrobe_item?imageUri=${processedUri}")
                                }
                            }) {
                                Icon(Icons.Default.Check, contentDescription = "Confirm", tint = customColor)
                            }
                            IconButton(onClick = {
                                cameraLaunched = true
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Cancel", tint = customColor)
                            }
                        }
                    } else {
                        Button(onClick = { context.handleCameraPermission() }) {
                            Text("Request Camera Permission")
                        }
                    }
                }
            }
        }


    }
}

fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val image = File.createTempFile(
        imageFileName,
        ".jpg",
        externalCacheDir
    )
    return image
}

fun removeBackgroundAndUpload(context: Context, imageUri: Uri, onComplete: (Uri) -> Unit) {
    val apiKey = "PaaXzUsVC9srkjC1ra8GpNv2"
    val file = context.createImageFile()
    val inputStream = context.contentResolver.openInputStream(imageUri)
    val outputStream = FileOutputStream(file)
    inputStream?.copyTo(outputStream)
    inputStream?.close()
    outputStream.close()

    val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("image_file", file.name, file.asRequestBody("image/*".toMediaTypeOrNull()))
        .addFormDataPart("size", "auto")
        .build()

    val request = okhttp3.Request.Builder()
        .url("https://api.remove.bg/v1.0/removebg")
        .addHeader("X-Api-Key", apiKey)
        .post(requestBody)
        .build()

    val client = OkHttpClient()
    client.newCall(request).enqueue(object : okhttp3.Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("RemoveBG", "Failed to remove background", e)
        }

        override fun onResponse(call: Call, response: okhttp3.Response) {
            if (response.isSuccessful) {
                val inputStream = response.body?.byteStream()
                val outputFile = context.createImageFile()
                val outputStream = FileOutputStream(outputFile)
                inputStream?.copyTo(outputStream)
                outputStream.close()
                inputStream?.close()

                val processedUri = FileProvider.getUriForFile(
                    context,
                    context.packageName + ".provider",
                    outputFile
                )
                Handler(Looper.getMainLooper()).post {
                    onComplete(processedUri)
                }
            } else {
                Log.e("RemoveBG", "Failed to remove background: ${response.message}")
            }
        }
    })
}