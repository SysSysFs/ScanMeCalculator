package com.example.scanmecalculator.domain.use_case

import com.example.scanmecalculator.domain.model.TextParserInfo
import com.example.scanmecalculator.domain.repository.StorageType
import com.example.scanmecalculator.domain.repository.TextParserInfoRepository

class SaveDataUseCase(private val textParserInfoRepository: TextParserInfoRepository) {
    suspend operator fun invoke(
        type: StorageType = StorageType.FILE,
        textParserInfo: TextParserInfo
    ) {
        textParserInfoRepository.saveTextParserInfo(type, textParserInfo)
    }
}