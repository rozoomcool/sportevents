//package com.example.pdfprocessor
//
//import com.example.utils.extractGenderAge
//import kotlinx.coroutines.*
//import org.apache.pdfbox.pdmodel.PDDocument
//import org.slf4j.LoggerFactory
//import java.io.File
//import java.net.HttpURLConnection
//import java.net.URL
//import java.util.concurrent.Executors
//
//class PdfProcessor(
////    private val grpcSender: GrpcSender
//) {
//
//    private val logger = LoggerFactory.getLogger(PdfProcessor::class.java)
//
//    /**
//     * Основной метод для обработки PDF.
//     */
//    fun processPdf() {
//        var tempFile: File? = null
//
//        try {
//            // Получение ссылки на PDF и загрузка файла
//            val link = getLink()
//            tempFile = downloadPdf(link)
//
//            // Определяем количество страниц в PDF
//            val document = PDDocument.load(tempFile)
//            val totalPages = document.numberOfPages
//            document.close()
//
//            // Разделяем страницы для параллельной обработки
//            val numWorkers = Runtime.getRuntime().availableProcessors()
//            val pageRanges = splitPages(totalPages, numWorkers)
//
//            // Обрабатываем страницы в пуле потоков
//            val executor = Executors.newFixedThreadPool(numWorkers)
//            val events = mutableListOf<SportEvent>()
//
//            runBlocking {
//                val jobs = pageRanges.map { range ->
//                    async(Dispatchers.IO) {
//                        processPageRange(tempFile, range)
//                    }
//                }
//                jobs.forEach { job ->
//                    try {
//                        events.addAll(job.await())
//                    } catch (e: Exception) {
//                        logger.error("Ошибка при обработке диапазона страниц: ${e.message}")
//                    }
//                }
//            }
//
//            // Постобработка для заполнения отсутствующих данных
//            processSportTypes(events)
//
//            // Отправляем данные через gRPC
////            grpcSender.sendEvents(events)
//            logger.info("Всего отправлено событий: ${events.size}")
//
//        } catch (e: Exception) {
//            logger.error("Общая ошибка в процессе обработки PDF: ${e.message}")
//        } finally {
//            tempFile?.delete()
//        }
//    }
//
//    /**
//     * Получение ссылки на PDF.
//     */
//    private fun getLink(): String {
//        // Имитация получения ссылки (заменить на ваш механизм)
//        return "https://example.com/path/to/file.pdf"
//    }
//
//    /**
//     * Загрузка PDF по ссылке.
//     */
//    private fun downloadPdf(url: String): File {
//        logger.info("Загрузка PDF по ссылке: $url")
//        val connection = URL(url).openConnection() as HttpURLConnection
//        connection.requestMethod = "GET"
//        connection.setRequestProperty("User-Agent", "Mozilla/5.0")
//
//        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
//            val tempFile = File.createTempFile("temp", ".pdf")
//            connection.inputStream.use { input ->
//                tempFile.outputStream().use { output ->
//                    input.copyTo(output)
//                }
//            }
//            logger.info("PDF успешно скачан: ${tempFile.absolutePath}")
//            return tempFile
//        } else {
//            throw Exception("Ошибка загрузки PDF. Код: ${connection.responseCode}")
//        }
//    }
//
//    /**
//     * Разделение страниц для обработки.
//     */
//    private fun splitPages(totalPages: Int, numWorkers: Int): List<String> {
//        val pagesPerWorker = totalPages / numWorkers
//        return (0 until numWorkers).map { i ->
//            val start = i * pagesPerWorker + 1
//            val end = if (i == numWorkers - 1) totalPages else (i + 1) * pagesPerWorker
//            "$start-$end"
//        }
//    }
//
//    /**
//     * Обработка диапазона страниц.
//     */
//    private fun processPageRange(file: File, pageRange: String): List<SportEvent> {
//        logger.debug("Начало обработки диапазона страниц: $pageRange")
//        val tables = PdfUtils.extractTables(file, pageRange)
//
//        val events = mutableListOf<SportEvent>()
//        var sportType = ""
//        var sportSubtype = ""
//
//        for (table in tables) {
//            val pageNumber = table.pageNumber
//            logger.debug("Обработка таблицы на странице $pageNumber")
//
//            for (row in table.rows) {
//                logger.debug("Данные строки: $row")
//
//                if (row.size < 5) row.addAll(List(5 - row.size) { "" })
//                val (col1, col2, col3, col4, col5) = row
//
//                when {
//                    col1.isDigitsOnly() -> {
//                        val event = SportEvent(
//                            id = col1,
//                            sportType = sportType,
//                            sportSubtype = sportSubtype,
//                            pageNumber = pageNumber.toInt(),
//                            description = col2 ?: "",
//                            location = col4 ?: "",
//                            participants = col5.toIntOrNull() ?: 0
//                        )
//                        events.add(event)
//                    }
//                    col1.isUpperCase() -> {
//                        sportType = col1
//                    }
//                    col1.isNotBlank() -> {
//                        sportSubtype = col1
//                    }
//                }
//            }
//        }
//
//        // Очистка строковых полей и извлечение поло-возрастной информации
//        events.forEach { event ->
//            event.description = event.description.trim()
//            event.location = event.location.trim()
//            event.genderAgeInfo = extractGenderAge(event.description)
//        }
//
//        return events
//    }
//
//    /**
//     * Постобработка для заполнения отсутствующих типов спорта.
//     */
//    private fun processSportTypes(events: List<SportEvent>) {
//        var lastSportType = ""
//        var lastSportSubtype = ""
//
//        for (event in events) {
//            if (event.sportType.isBlank()) {
//                event.sportType = lastSportType
//            } else {
//                lastSportType = event.sportType
//            }
//
//            if (event.sportSubtype.isBlank()) {
//                event.sportSubtype = lastSportSubtype
//            } else {
//                lastSportSubtype = event.sportSubtype
//            }
//        }
//    }
//}
//
//data class DatesRange(
//    var from: String = "",
//    var to: String = ""
//) {
//    override fun toString(): String {
//        return if (from.isNotBlank() || to.isNotBlank()) "$from - $to" else ""
//    }
//}
//
//data class SportEvent(
//    val pageNumber: Int,
//    val eventOrder: Int,
//
//    var id: String,
//    var sportType: String,
//    var sportSubtype: String,
//
//    var name: String = "",
//    var description: String = "",
//    var dates: DatesRange = DatesRange(),
//    var location: String = "",
//    var participants: Int = 0,
//
//    var genderAgeInfo: Map<String, Any> = emptyMap()
//) {
//    override fun toString(): String {
//        val details = listOfNotNull(
//            "id: $id",
//            "sport: $sportType",
//            "subtype: $sportSubtype",
//            if (name.isNotBlank()) "name: $name" else null,
//            if (dates.from.isNotBlank() || dates.to.isNotBlank()) "dates: $dates" else null,
//            if (description.isNotBlank()) "description: $description" else null,
//            if (location.isNotBlank()) "location: $location" else null,
//            if (participants > 0) "participants: $participants" else null,
//            "page: $pageNumber",
//            "order: $eventOrder"
//        )
//        return details.joinToString("\n")
//    }
//}
//
//fun sportEventToMap(event: SportEvent): Map<String, Any> {
//    return mapOf(
//        "pageNumber" to event.pageNumber,
//        "eventOrder" to event.eventOrder,
//        "id" to event.id,
//        "sportType" to event.sportType,
//        "sportSubtype" to event.sportSubtype,
//        "name" to event.name,
//        "description" to event.description,
//        "dates" to mapOf("from" to event.dates.from, "to" to event.dates.to),
//        "location" to event.location,
//        "participants" to event.participants,
//        "genderAgeInfo" to event.genderAgeInfo
//    )
//}
