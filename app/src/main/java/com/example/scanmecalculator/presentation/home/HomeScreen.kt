package com.example.scanmecalculator.presentation.home

import android.Manifest
import android.app.Activity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.scanmecalculator.BuildConfig
import com.example.scanmecalculator.R
import com.example.scanmecalculator.domain.repository.StorageType
import com.example.scanmecalculator.openAppSettings
import com.example.scanmecalculator.presentation.ui.theme.LocalSpacing

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    onLaunchCamera: (String) -> Unit,
    onImageSelected: (Pair<Uri?, String>) -> Unit,
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
    val textParserInfoList = viewModel.textParserInfoList.collectAsState()
    val storageType by viewModel.storageType.collectAsState()

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            onImageSelected(Pair(uri, storageType.label))
        }
    )

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
                onLaunchCamera(storageType.label)
            }
        }
    )

    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.refreshTextParserInfoList() }
    )

    Box(Modifier.pullRefresh(pullRefreshState)) {
        LazyColumn(
            modifier = modifier
                .padding(spacing.spaceMedium)
        ) {
            items(textParserInfoList.value) { textParserInfo ->
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.raw_text, textParserInfo.unfilteredText),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.input_text, textParserInfo.inputText),
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.result, textParserInfo.result),
                    textAlign = TextAlign.Start
                )
                Divider(modifier = Modifier.padding(vertical = spacing.spaceSmall))
            }
            item {
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    val radioOptions = StorageType.values().map { it.label }
                    Column(modifier = Modifier.selectableGroup()) {
                        radioOptions.forEach { label ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (storageType.label == label),
                                        onClick = {
                                            viewModel.onStorageTypeSelected(label)
                                        },
                                        role = Role.RadioButton
                                    )
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    modifier = Modifier.padding(end = 16.dp),
                                    selected = (storageType.label == label),
                                    onClick = null // null recommended for accessibility with screen readers
                                )
                                Text(text = label)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(spacing.spaceMedium))
                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {

                    if (BuildConfig.FLAVOR_imageSource == "camera") {
                        Button(
                            modifier = Modifier,
                            onClick = {
                                multiplePermissionResultLauncher.launch(permissionsToRequest)

                            }) {
                            Text(text = stringResource(id = R.string.launch_camera))
                        }
                    } else if (BuildConfig.FLAVOR_imageSource == "filesystem") {
                        Button(
                            modifier = Modifier,
                            onClick = {
                                singlePhotoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )

                            }) {
                            Text(text = stringResource(id = R.string.image_picker))
                        }
                    }
                }
            }
        }
        PullRefreshIndicator(isRefreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
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
