package com.govzcode.sportevents.util

import com.govzcode.sportevents.dto.SportEventDto
import com.govzcode.sportevents.util.UnsafeOkHttpClient
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
    val startDate: Date,
    val country: String,
    val participants: Int,
    val targetAudience: String,
    val secondEventDate: Date,
    val locations: List<String>,
    val disciplines: List<String>
)

@Component
@Scope("prototype")
class ProcessPdfLink {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    private fun parsePdfFromUrl(url: String): PDDocument {
        val client = UnsafeOkHttpClient.createUnsafeOkHttpClient()
        val request = Request.Builder()
            .url(url)
            .addHeader("User-Agent", "Mozilla/5.0")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Ошибка загрузки файла. Код ответа: ${response.code}")
            }

            return response.body?.byteStream()?.use { inputStream ->
                PDDocument.load(inputStream)
            } ?: throw Exception("Error get pdf exception")
        }
    }

    fun extractDate(dateString: String): Date? {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return try {
            dateFormat.parse(dateString)
        } catch (e: Exception) {
            null // если не удалось распарсить дату
        }
    }

    fun extractEventDetails(input: String, sportTitle: String): EventDetails {
        // Разбиваем строку по пробелам
        val parts = input.split(" ")

        // Извлекаем название
        val title = parts.takeWhile { !it.matches(Regex("\\d{2}\\.\\d{2}\\.\\d{4}")) }
            .joinToString(" ")

        // Находим первую дату
        val startDateString = parts.find { it.matches(Regex("\\d{2}\\.\\d{2}\\.\\d{4}")) }
        val startDate = extractDate(startDateString ?: "")!!

        // Страна идет после первой даты
        val country = parts.getOrNull(parts.indexOf(startDateString) + 1) ?: ""

        // Количество участников идет после страны
        val participants = parts.getOrNull(parts.indexOf(country) + 1)?.toIntOrNull() ?: 0

        // Целевая аудитория идет после количества участников
        val targetAudience =
            parts.dropWhile { it != country }.drop(2).takeWhile { !it.matches(Regex("\\d{2}\\.\\d{2}\\.\\d{4}")) }
                .joinToString(" ")

        // Вторая дата (дата события второго этапа)
        val secondEventDateString = parts.dropWhile { !it.matches(Regex("\\d{2}\\.\\d{2}\\.\\d{4}")) }
            .drop(1)
            .find { it.matches(Regex("\\d{2}\\.\\d{2}\\.\\d{4}")) }
        val secondEventDate = extractDate(secondEventDateString ?: "")!!

        // Локации — все после второй даты
        val locations = parts.dropWhile { it != secondEventDateString }
            .drop(1)
            .joinToString(" ")
            .split(",")
            .map { it.trim() }

        val filteredLocation = mutableListOf<String>()
        val disciplines = mutableListOf<String>()

        for (l in locations) {
            if (l.contains("класс", ignoreCase = true)
                || l.contains("дисциплин", ignoreCase = true)
                || l.matches(Regex("[a-zA-Z]"))
            ) {
                if (l.startsWith("г.") || l.startsWith("село")) {
                    filteredLocation.add("${l.split(" ")[1]}")
                } else {
                    val disc = l.uppercase()
                    disciplines.addAll(
                        disc.apply {
                            replace("ДИСЦИПЛИНА", "")
                            replace("ДИСЦИПЛИНЫ", "")
                            replace("КЛАСС", "")
                            trim()
                        }.split(" ").filter {
                            it.trim().isEmpty() && (it.contains(
                                "класс",
                                ignoreCase = false
                            ) || it.contains("дисцип", ignoreCase = false))
                        })
                }
            } else {
                filteredLocation.add(l.trim())
            }
        }

        return EventDetails(
            id = title.split(" ")[0],
            title = sportTitle,
            startDate = startDate,
            country = country,
            participants = participants,
            targetAudience = targetAudience,
            secondEventDate = secondEventDate,
            locations = filterStringsContainingEnglishLetters(filteredLocation),
            disciplines = disciplines
        )
    }

    fun filterStringsContainingEnglishLetters(strings: List<String>): List<String> {
        return strings.filter { it.contains(Regex("[a-zA-Z]")) }
    }

    private fun groupEvents(lines: List<String>): Map<String, String> {
        val eventMap = mutableMapOf<String, String>()
        var currentEventId: String? = null
        var currentEventDetails = StringBuilder()
        var previousIndex = 0
        var currentSportTitle = " "

        var i = 0
        for (line in lines) {
            if (line.startsWith("Стр.") || line.contains("состав", ignoreCase = true)) {
                if (line.contains("основной состав", ignoreCase = true)) {
                    currentSportTitle = lines[previousIndex]
                }
                i++
                continue
            }
            previousIndex = i

            // Ищем ID мероприятия (16 цифр в начале строки)
            val match = Regex("""^\d{16}""").find(line)
            if (match != null) {
                // Если ID найден, сохраняем текущую информацию
                if (currentEventId != null) {
                    eventMap["$currentEventId $currentSportTitle $currentEventId"] =
                        currentEventDetails.toString().trim()
                }

                // Начинаем новый блок для мероприятия
                currentEventId = match.value
                currentEventDetails = StringBuilder(line)
            } else {
                currentEventDetails.append(" ").append(line)
            }
            i++
        }

        if (currentEventId != null) {
            eventMap["$currentSportTitle $currentEventId"] = currentEventDetails.toString().trim()
        }

        return eventMap
    }

    fun getPdfData(url: String, onFind: (SportEventDto) -> Unit): List<SportEventDto> {
        this.logger.info("START")
        val document = parsePdfFromUrl(url)
        this.logger.info("DOWNLOADED")
        val events = mutableListOf<SportEventDto>()

        try {
            val stripper = PDFTextStripper()
            stripper.sortByPosition = true
            stripper.startPage = 1
            stripper.endPage = document.numberOfPages

            val text = stripper.getText(document)
            val lines = text.split("\n").map { it.trim() }.filter { it.isNotEmpty() }

            // Группируем события по ID
            val groupedEvents = groupEvents(lines)
            this.logger.info("GROUP")
            val results = mutableListOf<EventDetails>()

            groupedEvents.forEach {
                try {
                    this.logger.info("go go go")
                    // Извлекаем детали мероприятия
                    results.add(extractEventDetails(it.value, it.key.split(" ")[1]))
                } catch (_: Exception) {
                    logger.error("Ошибка при извлечении данных для события: ${it.key}")
                }
            }


            logger.info("Found $results.size")
            results.forEach() {
                onFind(
                    SportEventDto(
                        ekpId = it.id,
                        title = it.title,
                        targetAuditory = it.targetAudience,
                        disciplines = it.disciplines,
                        startsDate = it.startDate,
                        endsDate = it.secondEventDate,
                        country = it.country,
                        regions = it.locations,
                        numberOfParticipants = it.participants.toLong()
                    )
                )
            }

        } catch (e: Exception) {
            logger.error("Ошибка при обработке PDF: ${e.message}", e)
        } finally {
            document.close()
        }

        logger.info("Найдено ${events.size} событий")
        return events
    }
}
