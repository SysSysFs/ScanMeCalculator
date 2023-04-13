package com.example.scanmecalculator.domain.use_case

import com.example.scanmecalculator.domain.model.TextParserInfo
import com.example.scanmecalculator.domain.repository.StorageType
import com.example.scanmecalculator.domain.repository.TextParserInfoRepository
import kotlinx.coroutines.flow.Flow

class GetDataUseCase(private val repository: TextParserInfoRepository) {
    operator fun invoke(
        type: StorageType = StorageType.FILE,
    ): Flow<List<TextParserInfo>> {
        return repository.readTextParserInfoList(type)
    }
}