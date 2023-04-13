package com.example.scanmecalculator.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class TextParserInfoEntity(
    val unfilteredText: String,
    val inputText: String,
    val firstNumber: Double,
    val secondNumber: Double,
    val operator: Char?,
    @PrimaryKey val id: Int? = null
)