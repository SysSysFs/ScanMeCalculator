package com.example.scanmecalculator.di

import android.app.Application
import com.example.scanmecalculator.data.file_storage.FileStorage
import com.example.scanmecalculator.data.file_storage.FileStorage.Companion.FILE_NAME_TEXT_PARSER_INFO
import com.example.scanmecalculator.domain.repository.TextParserInfoRepository
import com.example.scanmecalculator.domain.use_case.GetDataUseCase
import com.example.scanmecalculator.domain.use_case.ReadTextUseCase
import com.example.scanmecalculator.domain.use_case.TakePictureUseCase
import com.example.scanmecalculator.domain.use_case.SaveDataUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import java.io.File


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

    @ViewModelScoped
    @Provides
    fun provideSaveDataUseCase(textParserInfoRepository: TextParserInfoRepository): SaveDataUseCase {
        return SaveDataUseCase(textParserInfoRepository)
    }

    @ViewModelScoped
    @Provides
    fun provideGetDataUseCase(textParserInfoRepository: TextParserInfoRepository): GetDataUseCase {
        return GetDataUseCase(textParserInfoRepository)
    }
}