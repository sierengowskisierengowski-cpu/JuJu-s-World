package com.jujusworld.screens

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.google.accompanist.permissions.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val camPermission = rememberPermissionState(android.Manifest.permission.CAMERA)
    var selectedFilter by remember { mutableStateOf("Hearts") }
    var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_FRONT_CAMERA) }
    var imageCaptureRef by remember { mutableStateOf<ImageCapture?>(null) }
    var flashOn by remember { mutableStateOf(false) }

    val filters = listOf("None", "Hearts", "Stars", "Rainbow", "Butterflies")
    val filterEmoji = mapOf("None" to "", "Hearts" to "💖", "Stars" to "⭐", "Rainbow" to "🌈", "Butterflies" to "🦋")

    LaunchedEffect(Unit) {
        if (!camPermission.status.isGranted) camPermission.launchPermissionRequest()
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (!camPermission.status.isGranted) {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("📷", fontSize = 64.sp)
                Spacer(Modifier.height(16.dp))
                Text("Camera permission needed", color = Color.White, fontSize = 18.sp)
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { camPermission.launchPermissionRequest() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC4899))
                ) { Text("Allow Camera") }
                TextButton(onClick = { navController.popBackStack() }) {
                    Text("← Go Back", color = Color.White)
                }
            }
            return@Box
        }

        // Camera preview
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                bindCamera(ctx, lifecycleOwner, previewView, cameraSelector) { ic -> imageCaptureRef = ic }
                previewView
            },
            update = { pv ->
                bindCamera(context, lifecycleOwner, pv, cameraSelector) { ic -> imageCaptureRef = ic }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Filter overlay
        if (selectedFilter != "None") {
            val emoji = filterEmoji[selectedFilter] ?: ""
            Box(Modifier.fillMaxSize()) {
                repeat(20) { i ->
                    val x = (i * 113) % 90 + 5
                    val y = (i * 79) % 80 + 5
                    Text(emoji, fontSize = (18 + i % 12).sp,
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.TopStart)
                            .padding(start = (x * 3.5).dp, top = (y * 5.2).dp)
                            .alpha(0.7f))
                }
            }
        }

        // Top bar
        Row(
            Modifier.fillMaxWidth().align(Alignment.TopStart)
                .background(Color(0x88000000)).padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
            }
            Text("📷  Camera", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(1f))
            IconButton(onClick = {
                cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA)
                    CameraSelector.DEFAULT_BACK_CAMERA else CameraSelector.DEFAULT_FRONT_CAMERA
            }) { Icon(Icons.Filled.FlipCameraAndroid, "Flip", tint = Color.White) }
        }

        // Filter picker
        Row(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 64.dp)
                .background(Color(0x88000000), RoundedCornerShape(20.dp)).padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { filter ->
                Box(
                    modifier = Modifier
                        .background(
                            if (filter == selectedFilter) Color(0xFFEC4899) else Color(0x44FFFFFF),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("${filterEmoji[filter]} $filter", fontSize = 12.sp, color = Color.White)
                }
            }
        }

        // Capture button
        Box(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable {
                        imageCaptureRef?.let { ic ->
                            capturePhoto(context, ic) { msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(Color(0xFFEC4899)))
            }
        }
    }
}

private fun bindCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    cameraSelector: CameraSelector,
    onCapture: (ImageCapture) -> Unit
) {
    val future = ProcessCameraProvider.getInstance(context)
    future.addListener({
        val provider = future.get()
        val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
        val imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
        onCapture(imageCapture)
        provider.unbindAll()
        try {
            provider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
        } catch (_: Exception) { }
    }, ContextCompat.getMainExecutor(context))
}

private fun capturePhoto(context: Context, imageCapture: ImageCapture, onResult: (String) -> Unit) {
    val name = "JuJu_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/JujusWorld")
    }
    val outputOptions = ImageCapture.OutputFileOptions.Builder(
        context.contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
    ).build()
    imageCapture.takePicture(
        outputOptions, ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) = onResult("📸 Photo saved!")
            override fun onError(exc: ImageCaptureException) = onResult("Error: ${exc.message}")
        }
    )
}
