package com.example.scanmecalculator.presentation.camera

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.scanmecalculator.R
import com.example.scanmecalculator.presentation.camera.CameraViewModel.Companion.emptyImageUri
import com.example.scanmecalculator.presentation.camera.components.CapturePictureButton
import com.example.scanmecalculator.presentation.ui.theme.LocalSpacing
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalCoilApi::class)
@Composable
fun CameraScreen(
    viewModel: CameraViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    val imageUri by viewModel.imageUri.collectAsState()
    val spacing = LocalSpacing.current

    var previewUseCase by remember {
        mutableStateOf<UseCase>(
            Preview.Builder().build()
        )
    }
    val imageCaptureUseCase by remember {
        mutableStateOf(
            ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build()
        )
    }

    Box(modifier = modifier) {
        if (imageUri != emptyImageUri) {
            Box(modifier = Modifier.padding(spacing.spaceMedium)) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = rememberImagePainter(imageUri),
                    contentDescription = stringResource(id = R.string.captured_image)
                )
                Button(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    onClick = {
                        viewModel.onRetakePicture()
                    }
                ) {
                    Text(stringResource(id = R.string.retake_picture))
                }
            }
        } else {
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                onUseCase = {
                    previewUseCase = it
                }
            )

            CapturePictureButton(
                modifier = Modifier
                    .size(100.dp)
                    .padding(spacing.spaceMedium)
                    .align(Alignment.BottomCenter),
                onClick = {
                    coroutineScope.launch {
                        viewModel.takePicture(imageCaptureUseCase, context.executor)
                    }
                }
            )
        }

    }

    LaunchedEffect(previewUseCase) {
        val cameraProvider = context.getCameraProvider()
        try {
            // Must unbind the use-cases before rebinding them.
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner, cameraSelector, previewUseCase, imageCaptureUseCase
            )
        } catch (ex: Exception) {
            Timber.e("Failed to bind camera use cases", ex)
        }
    }
}