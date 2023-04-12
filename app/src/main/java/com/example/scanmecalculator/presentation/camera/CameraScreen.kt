package com.example.scanmecalculator.presentation.camera

import android.widget.Space
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.scanmecalculator.R
import com.example.scanmecalculator.presentation.camera.CameraViewModel.Companion.emptyImageUri
import com.example.scanmecalculator.presentation.camera.components.CapturePictureButton
import com.example.scanmecalculator.presentation.ui.theme.LocalSpacing
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException

@OptIn(ExperimentalCoilApi::class)
@Composable
fun CameraScreen(
    viewModel: CameraViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
) {
    val context = LocalContext.current
    val spacing = LocalSpacing.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    val imageUri by viewModel.imageUri.collectAsState()
    val previewUseCase by viewModel.previewUseCase.collectAsState()
    val imageCaptureUseCase by viewModel.imageCaptureUseCase.collectAsState()
    val text by viewModel.text.collectAsState()

    LaunchedEffect(key1 = imageUri) {
        if (imageUri != emptyImageUri) {
            var image: InputImage? = null
            try {
                image = InputImage.fromFilePath(context, imageUri)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            viewModel.readText(image)
        }
    }

    Box(modifier = modifier) {
        if (imageUri != emptyImageUri) {
            Column(
                modifier = Modifier.padding(spacing.spaceMedium),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    painter = rememberImagePainter(imageUri),
                    contentDescription = stringResource(id = R.string.captured_image)
                )

                Spacer(modifier = Modifier.height(spacing.spaceMedium))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.raw_text, text?.unfilteredText ?: ""),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.input_text, text?.inputText ?: ""),
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.result, text?.result ?: 0.0),
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
                Button(
                    modifier = Modifier,
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
                    viewModel.onUseCase(it)
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