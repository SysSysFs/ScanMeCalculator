package com.example.scanmecalculator.domain.repository

import com.example.scanmecalculator.domain.model.TextParserInfo
import kotlinx.coroutines.flow.Flow

interface TextParserInfoRepository {
    suspend fun saveTextParserInfo(type: StorageType, textParserInfo: TextParserInfo)
    suspend fun readTextParserInfoList(type: StorageType): Flow<List<TextParserInfo>>
}

enum class StorageType {
    DATABASE,
    FILE
}