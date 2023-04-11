package com.example.scanmecalculator.di

import com.example.scanmecalculator.domain.use_case.ReadTextUseCase
import com.example.scanmecalculator.domain.use_case.TakePictureUseCase
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {
    @ViewModelScoped
    @Provides
    fun provideTakePictureUseCase(): TakePictureUseCase {
        return TakePictureUseCase()
    }

    @ViewModelScoped
    @Provides
    fun provideReadTextUseCase(): ReadTextUseCase {
        return ReadTextUseCase()
    }
}