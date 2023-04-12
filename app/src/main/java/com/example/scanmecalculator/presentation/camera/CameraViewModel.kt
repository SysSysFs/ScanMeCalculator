package com.example.scanmecalculator.presentation.camera

import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.example.scanmecalculator.MainActivity.Companion.emptyImageUri
import com.example.scanmecalculator.domain.model.TextParserInfo
import com.example.scanmecalculator.domain.use_case.ReadTextUseCase
import com.example.scanmecalculator.domain.use_case.TakePictureUseCase
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.util.concurrent.Executor
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val takePictureUseCase: TakePictureUseCase,
    private val readTextUseCase: ReadTextUseCase,
    private val textRecognizer: TextRecognizer
) : ViewModel() {
    private val _imageUri = MutableStateFlow(emptyImageUri)
    val imageUri = _imageUri.asStateFlow()

    private val _text: MutableStateFlow<TextParserInfo?> = MutableStateFlow(null)
    val text = _text.asStateFlow()

    private val _previewUseCase: MutableStateFlow<UseCase> =
        MutableStateFlow(Preview.Builder().build())
    val previewUseCase = _previewUseCase.asStateFlow()

    private val _imageCaptureUseCase: MutableStateFlow<ImageCapture> =
        MutableStateFlow(
            ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build()
        )
    val imageCaptureUseCase = _imageCaptureUseCase.asStateFlow()

    fun onUseCase(useCase: UseCase) {
        _previewUseCase.value = useCase
    }

    fun onRetakePicture() {
        _imageUri.value = emptyImageUri
    }

    suspend fun takePicture(imageCapture: ImageCapture, executor: Executor) {
        val file = takePictureUseCase.invoke(imageCapture, executor)
        _imageUri.value = file.toUri()
    }

    suspend fun readText(
        image: InputImage?
    ) {
        try {
            _text.value = readTextUseCase.invoke(textRecognizer, image)
        } catch (ex: Exception) {
            _text.value = null
            Timber.e("Failed to parse image to text", ex)
        }

    }
}