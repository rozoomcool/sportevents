package com.govzcode.sportevents.util

import com.govzcode.sportevents.dto.SportEventDto
import okhttp3.Request
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.util.*

data class EventDetails(
    val id: String,
    val title: String,
    val startDate: Date?,
    val country: String,
    val participants: Int,
    val targetAudience: List<String>,
    val regionsAndCities: List<String>,
    val disciplines: List<String>
)

fun extractEventDetails(input: String): EventDetails {
    val lines = input.split("\n") // Разбиваем на строки

    // Обрабатываем первую строку
    val firstLine = lines[0]
    val firstLineParts = firstLine.split(" ")

    val id = firstLineParts[0]
    val title = firstLineParts.subList(1, firstLineParts.size - 4).joinToString(" ")
    val startDateString = firstLineParts[firstLineParts.size - 4]
    val country = firstLineParts[firstLineParts.size - 3]
    val participants = firstLineParts[firstLineParts.size - 2].toInt()

    // Преобразуем строку с датой в объект Date
    val startDate = extractDate(startDateString)

    // Обрабатываем целевую аудиторию
    val targetAudienceString = lines[1]
    val targetAudience = targetAudienceString.split(",").map { it.trim() }

    // Обрабатываем даты целевой аудитории (если есть)
    val targetDateString = lines.getOrNull(2) ?: ""

    // Обрабатываем регионы и города (остальные строки после первой)
    val regionsAndCities = mutableListOf<String>()
    val disciplines = mutableListOf<String>()
    var isDisciplinesSection = false

    for (i in 2 until lines.size) {
        val line = lines[i]

        // Пропускаем пустые строки
        if (line.isBlank()) continue

        // Если строка содержит слово "Дисциплина" или выглядит как дисциплина
        if (line.contains("дисциплина", ignoreCase = true) || line.length < 50) {
            disciplines.add(line)
            isDisciplinesSection = true
        } else {
            // Все остальные строки добавляем в регионы/города
            if (!isDisciplinesSection) {
                regionsAndCities.add(line)
            } else {
                disciplines.add(line)
            }
        }
    }

    return EventDetails(
        id,
        title,
        startDate,
        country,
        participants,
        targetAudience,
        regionsAndCities,
        disciplines
    )
}

fun extractDate(dateString: String): Date? {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return try {
        dateFormat.parse(dateString)
    } catch (e: Exception) {
        null // если не удалось распарсить дату
    }
}

@Component
@Scope("prototype")
class ProcessPdfLink(
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private fun parsePdfFromUrl(url: String): PDDocument {
        val client = UnsafeOkHttpClient.createUnsafeOkHttpClient()
        val request = Request.Builder()
            .url(url)
            .addHeader("User-Agent", "Mozilla/5.0")
//        .addHeader("content-type", "application/pdf")
            .build()

        var result: String
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Ошибка загрузки файла. Код ответа: ${response.code}")
            }

            return response.body?.byteStream()?.use { inputStream ->
                PDDocument.load(inputStream)
            } ?: throw Exception("Error get pdf exception")

        }
    }

    // Функция для группировки строк по ID
    fun groupEvents(lines: List<String>): Map<String, String> {
        val eventMap = mutableMapOf<String, String>()
        var currentEventId: String? = null
        var currentEventDetails = StringBuilder()

        for (line in lines) {
            // Пропускаем строки, начинающиеся с "Стр." или содержащие слово "состав"
            if (line.startsWith("Стр.") || line.contains("состав", ignoreCase = true)) {
                continue
            }

            // Пытаемся найти ID мероприятия
            val match = Regex("""^\d{16}""").find(line)
            if (match != null) {
                // Если нашли новый ID мероприятия, сохраняем текущий и начинаем новый
                if (currentEventId != null) {
                    eventMap[currentEventId] = currentEventDetails.toString().trim()
                }

                // Устанавливаем новый ID мероприятия
                currentEventId = match.value
                currentEventDetails = StringBuilder(line)  // Начинаем с новой строки
            } else {
                // Если строка не содержит ID мероприятия, добавляем её к текущему мероприятию
                currentEventDetails.append(" ").append(line)
            }
        }

        // Добавляем последний собранный блок (если есть)
        if (currentEventId != null) {
            eventMap[currentEventId] = currentEventDetails.toString().trim()
        }

        return eventMap
    }

    fun getPdfData(url: String): List<SportEventDto> {
        val document = parsePdfFromUrl(url)
        val events = mutableListOf<SportEventDto>()
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

        try {
            val stripper = PDFTextStripper()
            stripper.sortByPosition = true
            stripper.startPage = 1
            stripper.endPage = document.numberOfPages

            val text = stripper.getText(document)
            val lines = text.split("\n").map { it.trim() }.filter { it.isNotEmpty() }


            val groupedEvents = groupEvents(lines)
            val data = groupedEvents.map {
                extractEventDetails(it.value)
            }


//            lines.forEach { line ->
//                when {
//                    line.matches(Regex("^[А-Я\\s]+$")) -> {
//                        currentDiscipline = line
//                    }
//
//                    line.matches(Regex("^\\d+\\s+.*")) -> {
//                        if (currentEventFirstLine != null) {
//                            val event = parseEvent(
//                                currentEventFirstLine,
//                                currentEventSecondLine,
//                                descriptionLines,
//                                currentDiscipline,
//                                dateFormat
//                            )
//                            event?.let { events.add(it) }
//                        }
//
//                        currentEventFirstLine = line
//                        currentEventSecondLine = null
//                        descriptionLines.clear()
//                        lineCounter = 0
//                    }
//
//                    else -> {
//                        if (lineCounter == 0 && currentEventFirstLine != null) {
//                            currentEventSecondLine = line
//                            lineCounter++
//                        } else {
//                            descriptionLines.add(line)
//                        }
//                    }
//                }
//            }
//
//            if (currentEventFirstLine != null) {
//                val event = parseEvent(
//                    currentEventFirstLine,
//                    currentEventSecondLine,
//                    descriptionLines,
//                    currentDiscipline,
//                    dateFormat
//                )
//                event?.let { events.add(it) }
//            }
        } catch (e: Exception) {
            logger.error("Ошибка при обработке PDF: ${e.message}", e)
        } finally {
            document.close()
        }

        logger.info("Найдено ${events.size} событий")
        return events
    }

    private fun parseEvent(
        firstLine: String?,
        secondLine: String?,
        descriptionLines: List<String>,
        discipline: String,
        dateFormat: SimpleDateFormat
    ): SportEventDto? {
        if (firstLine.isNullOrEmpty()) return null

        val parts = firstLine.split("\\s+".toRegex())
        if (parts.size < 4) return null

        val ekpId = parts[0]
        val dateIndex = parts.indexOfFirst { it.matches(Regex("\\d{2}\\.\\d{2}\\.\\d{4}")) }
        val participantsIndex = parts.indexOfLast { it.matches(Regex("\\d+")) }

        if (dateIndex == -1) {
            logger.error("Ошибка: дата не найдена в строке: $firstLine")
            return null
        }

        val startsDate = dateFormat.parse(parts[dateIndex])
        val participants = if (participantsIndex != -1) parts[participantsIndex].toLongOrNull() ?: 0L else 0L

        var country = ""
        var region = ""
        var city = ""
        var program = ""
        var endsDate: Date? = null

        if (secondLine != null) {
            val secondParts = secondLine.split("\\s+".toRegex())
            val dateEndIndex = secondParts.indexOfFirst { it.matches(Regex("\\d{2}\\.\\d{2}\\.\\d{4}")) }

            if (dateEndIndex != -1) {
                endsDate = dateFormat.parse(secondParts[dateEndIndex])
                region = secondParts.subList(dateEndIndex + 1, secondParts.size).joinToString(" ").trim()

                val locationParts = region.split(",")
                if (locationParts.size > 1) {
                    country = "РОССИЯ"
                    city = locationParts.last().trim()
                    region = locationParts.dropLast(1).joinToString(",").trim()
                }
            }
        }

        val targetAuditory = extractTargetAuditory(descriptionLines)

        val filteredDescription = filterProgramContent(descriptionLines, targetAuditory)
        program = if (filteredDescription.isNotEmpty()) filteredDescription.joinToString(" ").trim() else ""


        return SportEventDto(
            ekpId = ekpId,
            targetAuditory = targetAuditory,
            discipline = discipline,
            program = program,
            startsDate = startsDate,
            endsDate = endsDate ?: startsDate,
            country = country,
            region = if (city.isEmpty()) region else "$region, $city",
            city = city,
            numberOfParticipants = participants
        )
    }

    private fun filterProgramContent(descriptionLines: List<String>, targetAuditory: String): List<String> {
        val targetAuditoryParts = targetAuditory.split(", ").map { it.lowercase() }
        return descriptionLines.filter { line ->
            targetAuditoryParts.none { part -> line.lowercase().contains(part) }
        }
    }

    private fun extractTargetAuditory(descriptionLines: List<String>): String {
        val description = descriptionLines.joinToString(" ").lowercase()

        val genderMapping = listOf(
            "женщины", "мужчины", "юниоры", "юниорки", "девушки", "девочки", "юноши", "мальчики"
        )

        val genders = genderMapping.filter { description.contains(it) }.joinToString(", ")

        val agePattern = Regex("(от\\s+\\d+\\s+лет|до\\s+\\d+\\s+лет|\\d+\\s*-\\s*\\d+\\s+лет|\\d+\\s+лет)")
        val ageMatches = agePattern.findAll(description).map { it.value }.joinToString(", ")

        return if (genders.isNotEmpty() || ageMatches.isNotEmpty()) {
            listOf(genders, ageMatches).filter { it.isNotEmpty() }.joinToString(", ")
        } else {
            "Не указано"
        }
    }
}