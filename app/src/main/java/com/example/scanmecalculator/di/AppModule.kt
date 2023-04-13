package com.example.scanmecalculator.di

import android.app.Application
import androidx.room.Room
import com.example.scanmecalculator.data.database.TextParserInfoDao
import com.example.scanmecalculator.data.database.TextParserInfoDatabase
import com.example.scanmecalculator.data.file_storage.FileStorage
import com.example.scanmecalculator.data.repository.TextParserInfoRepositoryImpl
import com.example.scanmecalculator.domain.repository.TextParserInfoRepository
import com.example.scanmecalculator.domain.use_case.SaveDataUseCase
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideTextRecognizer(): TextRecognizer {
        return TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    @Singleton
    @Provides
    fun provideFileStorage(
        app: Application
    ): FileStorage {
        return FileStorage(
            app = app
        )
    }

    @Provides
    @Singleton
    fun provideTextParserInfoDatabase(app: Application): TextParserInfoDatabase {
        return Room.databaseBuilder(
            app,
            TextParserInfoDatabase::class.java,
            "text_parser_info_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideTextParserInfoRepository(
        db: TextParserInfoDatabase,
        fileStorage: FileStorage
    ): TextParserInfoRepository {
        return TextParserInfoRepositoryImpl(
            dao = db.dao,
            fileStorage = fileStorage
        )
    }
}