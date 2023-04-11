package com.example.scanmecalculator.presentation.camera

import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.example.scanmecalculator.domain.use_case.TakePictureUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.util.concurrent.Executor
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val takePictureUseCase: TakePictureUseCase
) : ViewModel() {
    private val _imageUri = MutableStateFlow(emptyImageUri)
    val imageUri = _imageUri.asStateFlow()

    fun onRetakePicture() {
        _imageUri.value = emptyImageUri
    }

    fun onTakePictureSuccess(file: File) {
        _imageUri.value = file.toUri()
    }

    suspend fun takePicture(imageCapture: ImageCapture, executor: Executor): File {
        val file =  takePictureUseCase.invoke(imageCapture, executor)
        onTakePictureSuccess(file)
        return file
    }

    companion object {
        val emptyImageUri: Uri = Uri.parse("file://dev/null")
    }

}