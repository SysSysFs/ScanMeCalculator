package com.example.scanmecalculator.domain.model

data class TextParserInfo(
    val unfilteredText: String,
    val inputText: String = "",
    val firstNumber: Double = 0.0,
    val secondNumber: Double = 0.0,
    val operator: Char? = null
) {
    val result
        get() = when (operator) {
            '+' -> {
                firstNumber + secondNumber
            }
            '-' -> {
                firstNumber - secondNumber
            }
            '*' -> {
                firstNumber * secondNumber
            }
            '/' -> {
                if (secondNumber == 0.0) {
                    throw UnsupportedOperationException("Cannot divide by zero");
                }
                firstNumber / secondNumber
            }
            else -> {
                0.0
            }
        }
}
