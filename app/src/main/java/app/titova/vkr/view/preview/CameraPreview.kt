package app.titova.vkr.view.preview

import android.Manifest
import android.content.Context
import android.util.Log
import android.util.Size
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import app.titova.vkr.util.rememberPermissionState
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun CameraPreview(
    onImageCaptured: (File) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val imageCapture = remember { mutableStateOf<ImageCapture?>(null) }
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val permissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(cameraProviderFuture) {
        if (permissionState.hasPermission) {
            setupCamera(cameraProviderFuture, lifecycleOwner, context, imageCapture, cameraExecutor, onImageCaptured)
        } else {
            permissionState.launchPermissionRequest()
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        val previewView = remember { PreviewView(context) }
        AndroidView(
            { previewView },
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            val capture = imageCapture.value ?: return@Button
            val photoFile = File(context.externalMediaDirs.first(), "photo_${System.currentTimeMillis()}.jpg")
            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

            capture.takePicture(
                outputOptions,
                cameraExecutor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        onImageCaptured(photoFile)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e("CameraPreview", "Photo capture failed: ${exception.message}", exception)
                    }
                }
            )
        }) {
            Text("Capture")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            // Implement loading image from gallery or other sources
        }) {
            Text("Load Image")
        }
    }
}

private fun setupCamera(
    cameraProviderFuture: ListenableFuture<ProcessCameraProvider>,
    lifecycleOwner: LifecycleOwner,
    context: Context,
    imageCapture: MutableState<ImageCapture?>,
    cameraExecutor: ExecutorService,
    onImageCaptured: (File) -> Unit
) {
    val cameraProvider = cameraProviderFuture.get()
    val previewView = PreviewView(context)

    val preview = Preview.Builder()
        .build()
        .also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

    imageCapture.value = ImageCapture.Builder()
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
        .setTargetResolution(Size(640, 480))
        .build()

    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
        .build()

    try {
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture.value
        )
    } catch (exc: Exception) {
        Log.e("CameraPreview", "Use case binding failed", exc)
    }
}
