package com.example.scanmecalculator.data.repository

import com.example.scanmecalculator.data.database.TextParserInfoDao
import com.example.scanmecalculator.data.file_storage.FileStorage
import com.example.scanmecalculator.domain.model.TextParserInfo
import com.example.scanmecalculator.domain.repository.StorageType
import com.example.scanmecalculator.domain.repository.TextParserInfoRepository
import com.example.scanmecalculator.mapper.toTextParserInfo
import com.example.scanmecalculator.mapper.toTextParserInfoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class TextParserInfoRepositoryImpl(
    private val dao: TextParserInfoDao,
    private val fileStorage: FileStorage,
) : TextParserInfoRepository {
    override suspend fun saveTextParserInfo(type: StorageType, textParserInfo: TextParserInfo) {
        when (type) {
            StorageType.DATABASE -> {
                dao.insertTextParserInfo(textParserInfo.toTextParserInfoEntity())
            }
            StorageType.FILE -> {
                fileStorage.saveTextParserInfo(textParserInfo)
            }
        }
    }

    override suspend fun readTextParserInfoList(type: StorageType): Flow<List<TextParserInfo>> {
        return when (type) {
            StorageType.DATABASE -> {
                dao.getTextParserInfoList()
                    .map { entities ->
                        entities.map {
                            it.toTextParserInfo()
                        }
                    }
            }
            StorageType.FILE -> {
                flowOf(fileStorage.readTextParserInfoList())
            }
        }
    }
}