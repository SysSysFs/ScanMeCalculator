package com.example.scanmecalculator.presentation.image_picker

import androidx.lifecycle.ViewModel
import com.example.scanmecalculator.domain.model.TextParserInfo
import com.example.scanmecalculator.domain.use_case.ReadTextUseCase
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognizer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ImagePickerResultViewModel @Inject constructor(
    private val readTextUseCase: ReadTextUseCase,
    private val textRecognizer: TextRecognizer
) : ViewModel() {
    private val _text: MutableStateFlow<TextParserInfo?> = MutableStateFlow(null)
    val text = _text.asStateFlow()

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