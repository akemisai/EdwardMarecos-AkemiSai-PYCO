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
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.pyco.app.MainActivity
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


@Composable
fun UploadScreen(
    navController: NavHostController
) {
    val context = LocalContext.current as MainActivity
    val permissionGranted = context.isCameraPermissionGranted.collectAsState().value

    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var cameraLaunched by remember { mutableStateOf(false) }

    val cameraLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { success ->
            if (success) {
                Log.d("UploadScreen", "Image captured successfully: $capturedImageUri")
                capturedImageUri?.let { uri ->
                    removeBackgroundAndUpload(context, uri) { processedUri ->
                        navController.navigate("add_wardrobe_item?imageUri=${processedUri}")
                    }
                }
            } else {
                Toast.makeText(context, "Failed to capture image", Toast.LENGTH_SHORT).show()
                navController.navigate("home")
            }
            cameraLaunched = false
        }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show()
            val file = context.createImageFile()
            val uri = FileProvider.getUriForFile(
                context,
                context.packageName + ".provider",
                file
            )
            capturedImageUri = uri
            cameraLauncher.launch(uri)
            cameraLaunched = true
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(permissionGranted) {
        if (permissionGranted && !cameraLaunched) {
            val file = context.createImageFile()
            val uri = FileProvider.getUriForFile(
                context,
                context.packageName + ".provider",
                file
            )
            capturedImageUri = uri
            cameraLauncher.launch(uri)
            cameraLaunched = true
        } else if (!permissionGranted) {
            permissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }
    if (capturedImageUri?.path?.isNotEmpty() == true) {
        Image(
            modifier = Modifier.padding(16.dp, 8.dp),
            painter = rememberAsyncImagePainter(capturedImageUri),
            contentDescription = null
        )
    } else {
        Image(
            modifier = Modifier.padding(16.dp, 8.dp),
            painter = rememberAsyncImagePainter(capturedImageUri),
            contentDescription = null
        )
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