package component

import dto.SportEventDto
import okhttp3.Request
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import utils.UnsafeOkHttpClient
import java.text.SimpleDateFormat
import java.util.*

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

            var currentDiscipline = ""
            var currentEventFirstLine: String? = null
            var currentEventSecondLine: String? = null
            val descriptionLines = mutableListOf<String>()
            var lineCounter = 0

            lines.forEach { line ->
                when {
                    line.matches(Regex("^[А-Я\\s]+$")) -> {
                        currentDiscipline = line
                    }

                    line.matches(Regex("^\\d+\\s+.*")) -> {
                        if (currentEventFirstLine != null) {
                            val event = parseEvent(
                                currentEventFirstLine,
                                currentEventSecondLine,
                                descriptionLines,
                                currentDiscipline,
                                dateFormat
                            )
                            event?.let { events.add(it) }
                        }

                        currentEventFirstLine = line
                        currentEventSecondLine = null
                        descriptionLines.clear()
                        lineCounter = 0
                    }

                    else -> {
                        if (lineCounter == 0 && currentEventFirstLine != null) {
                            currentEventSecondLine = line
                            lineCounter++
                        } else {
                            descriptionLines.add(line)
                        }
                    }
                }
            }

            if (currentEventFirstLine != null) {
                val event = parseEvent(
                    currentEventFirstLine,
                    currentEventSecondLine,
                    descriptionLines,
                    currentDiscipline,
                    dateFormat
                )
                event?.let { events.add(it) }
            }
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