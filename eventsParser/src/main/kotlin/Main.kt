import config.AppConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.security.SecureRandom
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

fun main(args: Array<String>) {
//    val context = AnnotationConfigApplicationContext(AppConfig::class.java)
//    println(context.getBean(String::class.java))
    val pdfUrl = "https://storage.minsport.gov.ru/cms-uploads/cms/II_chast_EKP_2024_14_11_24_65c6deea36.pdf"
    parsePdfFromUrl(pdfUrl)

    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Program arguments: ${args.joinToString()}")
}

fun createUnsafeOkHttpClient(): OkHttpClient {
    try {
        // Создаём TrustManager, который игнорирует проверки сертификатов
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = arrayOf()
        })

        // Создаём SSLContext, использующий наш TrustManager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

        // Создаём SSLSocketFactory с этим SSLContext
        val sslSocketFactory = sslContext.socketFactory

        // Возвращаем клиент OkHttp, отключив проверку имени хоста
        return OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true } // Отключаем проверку имени хоста
            .build()
    } catch (e: Exception) {
        throw RuntimeException(e)
    }
}


fun parsePdfFromUrl(url: String): String? {
    val client = createUnsafeOkHttpClient()
    val request = Request.Builder()
        .url(url)
        .addHeader("User-Agent", "Mozilla/5.0",)
//        .addHeader("content-type", "application/pdf")
        .build()

    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
            throw Exception("Ошибка загрузки файла. Код ответа: ${response.code}")
        }

        return response.body?.byteStream()?.use { inputStream ->
            PDDocument.load(inputStream).use { document ->
                val text = PDFTextStripper().getText(document)
                println("Извлечённый текст из PDF:\n${text.subSequence(0, 1000)}")
                text
            }
        }
    }
}

fun processPdfFromUrl(url: String) {
    try {
        // Указываем путь для временного сохранения файла
        val tempFilePath = "temp_downloaded_file.pdf"

        // Загружаем файл
        val pdfFile = downloadPdf(url, tempFilePath)
        println("Файл успешно загружен: ${pdfFile.absolutePath}")

        // Парсим PDF
        val events = parsePdf(pdfFile.absolutePath)
        events.forEach { event ->
            println("--------------")
            println(event)
            println("--------------")
        }

        // Удаляем временный файл после обработки
        pdfFile.delete()
    } catch (e: Exception) {
        println("--------------")
        println("Ошибка: ${e.message}")
        println("--------------")
    }
}

fun downloadPdf(url: String, outputFilePath: String): File {
    val client = createUnsafeOkHttpClient()
    val request = Request.Builder()
        .url(url)
        .addHeader("User-Agent", "Mozilla/5.0")
        .build()

    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
            throw Exception("Ошибка загрузки файла. Код ответа: ${response.code}")
        }

        // Сохраняем файл локально
        val file = File(outputFilePath)
        response.body?.byteStream()?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        return file
    }
}

fun parsePdf(filePath: String): List<SportEvent> {
    val events = mutableListOf<SportEvent>()

    // Читаем текст из PDF
    val text = PDDocument.load(File(filePath)).use { document ->
        PDFTextStripper().getText(document)
    }

    // Регулярное выражение для поиска событий
    val eventRegex = Regex(
        """(\d{16})\s+(.+?)\n(.+?)\n(.+?)\n(\d{2}\.\d{2}\.\d{4})\n(\d{2}\.\d{2}\.\d{4})\n(РОССИЯ)\n(.+?),\s+(.+?)\n(\d+)"""
    )

    // Ищем все совпадения
    eventRegex.findAll(text).forEach { matchResult ->
        val (id, name, participantsInfo, classDiscipline, startDate, endDate, country, region, city, participantsCount) =
            matchResult.destructured

        events.add(
            SportEvent(
                id = id,
                name = name,
                participantsInfo = participantsInfo,
                classDiscipline = classDiscipline,
                startDate = startDate,
                endDate = endDate,
                country = country,
                regionAndCity = "$region, $city",
                participantsCount = participantsCount.toInt()
            )
        )
    }

    return events
}

data class SportEvent(
    val id: String,
    val name: String,
    val participantsInfo: String,
    val classDiscipline: String,
    val startDate: String,
    val endDate: String,
    val country: String,
    val regionAndCity: String,
    val participantsCount: Int
)