package com.example.scanmecalculator.mapper

import com.example.scanmecalculator.domain.model.TextParserInfo

fun String.toTextParserInfo(): TextParserInfo {
    val operatorPattern = "(\\*|\\+|\\/|-)"
    val expression = Regex("\\d+$operatorPattern\\d+").find(this)?.value ?: ""
    val numbers = expression
        .split(regex = Regex(operatorPattern))
        .map { it.parseDouble() }
    val operator = Regex(operatorPattern).find(expression)?.value?.firstOrNull()

    if (numbers.size == 2 && operator != null) {
        val firstNumber = numbers.firstOrNull() ?: 0.0
        val secondNumber = numbers.lastOrNull() ?: 0.0

        return TextParserInfo(
            unfilteredText = this,
            inputText = expression,
            firstNumber = firstNumber,
            secondNumber = secondNumber,
            operator = operator
        )
    }

    return TextParserInfo(unfilteredText = this)
}

fun String.parseDouble(): Double {
    return if (isBlank()) 0.0 else toDouble()
}