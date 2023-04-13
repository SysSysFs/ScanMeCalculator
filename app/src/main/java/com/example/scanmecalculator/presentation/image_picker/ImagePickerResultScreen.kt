package com.example.scanmecalculator.presentation.image_picker

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.example.scanmecalculator.MainActivity
import com.example.scanmecalculator.R
import com.example.scanmecalculator.domain.repository.StorageType
import com.example.scanmecalculator.presentation.camera.CameraViewModel
import com.example.scanmecalculator.presentation.ui.theme.LocalSpacing
import com.google.mlkit.vision.common.InputImage
import java.io.IOException

@Composable
fun ImagePickerResultScreen(
    storageType: StorageType,
    imageUri: Uri,
    viewModel: ImagePickerResultViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val text by viewModel.text.collectAsState()

    LaunchedEffect(key1 = imageUri)
    {
        if (imageUri != MainActivity.emptyImageUri) {
            var image: InputImage? = null
            try {
                image = InputImage.fromFilePath(context, imageUri)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            viewModel.readText(storageType, image)
        }
    }

    Box(modifier = modifier) {
        if (imageUri != MainActivity.emptyImageUri) {
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
                    contentDescription = stringResource(id = R.string.image)
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
            }
        }
    }
}