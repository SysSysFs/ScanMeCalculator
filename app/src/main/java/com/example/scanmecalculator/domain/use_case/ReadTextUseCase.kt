package com.example.scanmecalculator.domain.use_case

import com.example.scanmecalculator.domain.model.TextParserInfo
import com.example.scanmecalculator.mapper.toTextParserInfo
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognizer
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ReadTextUseCase() {
    suspend operator fun invoke(
        recognizer: TextRecognizer,
        image: InputImage?
    ): TextParserInfo = suspendCoroutine { continuation ->
        if (image != null) {
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val unfilteredText = visionText.text
                    val textParserInfo = unfilteredText.toTextParserInfo()
                    continuation.resume(textParserInfo)
                }
                .addOnFailureListener { e ->
                    continuation.resume(TextParserInfo(unfilteredText = ""))
                }
        } else {
            continuation.resume(TextParserInfo(unfilteredText = ""))
        }
    }
}