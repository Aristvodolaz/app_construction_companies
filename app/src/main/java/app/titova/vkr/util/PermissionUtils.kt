package app.titova.vkr.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun rememberPermissionState(permission: String): PermissionState {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        // Handle the result here
    }
    val isPermissionGranted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    return remember { PermissionState(isPermissionGranted, permissionLauncher) }
}

data class PermissionState(
    val hasPermission: Boolean,
    val launcher: ActivityResultLauncher<String>
) {
    fun launchPermissionRequest() {
        launcher.launch(Manifest.permission.CAMERA)
    }
}
