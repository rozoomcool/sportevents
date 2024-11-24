package com.govzcode.sportevents.util

import java.util.regex.Pattern

fun containsDate(input: String): Boolean {
    val datePattern = "\\d{4}-\\d{2}-\\d{2}" // Регулярное выражение для даты в формате yyyy-MM-dd
    val pattern = Pattern.compile(datePattern)
    val matcher = pattern.matcher(input)
    return matcher.find()
}

fun startsWith16Digits(input: String): Boolean {
    // Регулярное выражение для проверки начала строки с 16 цифр
    val regex = "^\\d{16}".toRegex()

    // Проверяем, соответствует ли строка регулярному выражению
    return input.matches(regex)
}

fun containsAnyFromList(input: String, patterns: List<String>): Boolean {
    return patterns.any { input.contains(it) }
}

fun containsMatchFromList(input: String, patterns: List<String>): Boolean {
    // Объединяем все элементы списка в одно регулярное выражение
    val regex = patterns.joinToString("|") { "\\b$it\\b" }.toRegex()

    // Проверяем, есть ли совпадение с регулярным выражением
    return regex.containsMatchIn(input)
}