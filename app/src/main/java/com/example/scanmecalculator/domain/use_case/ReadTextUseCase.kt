package com.example.scanmecalculator.domain.use_case

import android.content.Context
import androidx.camera.lifecycle.ProcessCameraProvider
import com.example.scanmecalculator.presentation.camera.executor
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognizer
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ReadTextUseCase() {
    suspend operator fun invoke(
        recognizer: TextRecognizer,
        image: InputImage?
    ): String = suspendCoroutine { continuation ->
        if (image != null) {
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    continuation.resume(visionText.text)
                }
                .addOnFailureListener { e ->
                    continuation.resume("")
                }
        }
        else{
            continuation.resume("")
        }
    }
}