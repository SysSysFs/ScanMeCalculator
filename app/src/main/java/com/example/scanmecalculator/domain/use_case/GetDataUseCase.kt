package com.example.scanmecalculator.domain.use_case

import com.example.scanmecalculator.domain.model.TextParserInfo
import com.example.scanmecalculator.domain.repository.StorageType
import com.example.scanmecalculator.domain.repository.TextParserInfoRepository
import kotlinx.coroutines.flow.Flow

class GetDataUseCase() {
    suspend operator fun invoke(
        type: StorageType = StorageType.FILE,
        storage: TextParserInfoRepository
    ): Flow<List<TextParserInfo>> {
        return storage.readTextParserInfoList(type)
    }
}