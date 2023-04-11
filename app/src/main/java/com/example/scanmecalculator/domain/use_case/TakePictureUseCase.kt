package com.example.scanmecalculator.domain.use_case

import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.time.LocalDate
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class TakePictureUseCase() {

    suspend operator fun invoke(
        imageCapture: ImageCapture,
        executor: Executor
    ): File  {
        val photoFile = withContext(Dispatchers.IO) {
            kotlin.runCatching {
                File.createTempFile("image", "jpg")
            }.getOrElse { ex ->
                Timber.e("Failed to create temporary file", ex)
                File("/dev/null")
            }
        }

        return suspendCoroutine { continuation ->
            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
            imageCapture.takePicture(
                outputOptions, executor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        continuation.resume(photoFile)
                    }

                    override fun onError(ex: ImageCaptureException) {
                        Timber.e("Image capture failed", ex)
                        continuation.resumeWithException(ex)
                    }
                }
            )
        }
    }
}