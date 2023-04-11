package com.example.scanmecalculator.presentation.home

import android.Manifest
import android.app.Activity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.scanmecalculator.R
import com.example.scanmecalculator.openAppSettings
import com.example.scanmecalculator.presentation.ui.theme.LocalSpacing

@Composable
fun HomeScreen(
    onLaunchCamera: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val permissionsToRequest = arrayOf(
        Manifest.permission.CAMERA
    )

    val context = LocalContext.current
    val activity = context as Activity
    val dialogQueue = viewModel.visiblePermissionDialogQueue.collectAsState()
    val spacing = LocalSpacing.current

    val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            var allPermissionsGranted = true
            permissionsToRequest.forEach { permission ->
                var isGranted = true
                if (perms.containsKey(permission)) {
                    isGranted = perms[permission] == true
                    viewModel.onPermissionResult(
                        permission = permission,
                        isGranted = isGranted
                    )
                }
                allPermissionsGranted = allPermissionsGranted && isGranted
            }

            if (allPermissionsGranted) {
                onLaunchCamera()
            }
        }
    )

    Box(modifier = modifier.padding(spacing.spaceMedium))
    {
        Button(
            modifier = Modifier.align(
                Alignment.BottomCenter
            ),
            onClick = {
                multiplePermissionResultLauncher.launch(permissionsToRequest)
            }) {
            Text(text = stringResource(id = R.string.launch_camera))
        }
    }

    dialogQueue
        .value
        .reversed()
        .forEach { permission ->
            PermissionDialog(
                permissionTextProvider = when (permission) {
                    Manifest.permission.CAMERA -> {
                        CameraPermissionTextProvider()
                    }
                    else -> return@forEach
                },
                isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                    activity,
                    permission
                ),
                onDismiss = viewModel::dismissDialog,
                onOkClick = {
                    viewModel.dismissDialog()
                    multiplePermissionResultLauncher.launch(
                        arrayOf(permission)
                    )
                },
                onGoToAppSettingsClick = activity::openAppSettings
            )
        }
}
