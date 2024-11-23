package service

import exception.ParserException
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.regex.Pattern


class ParseLinksService {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    private fun openPage(driver: WebDriver, url: String) {
        try {
            logger.info("Открываем страницу: $url")
            driver.get(url)
        } catch (e: Exception) {
            throw ParserException("Не удалось открыть сайт")
        }

    }

    private fun getHead(driver: WebDriver): WebElement {
        return try {
            driver.findElement(By.xpath("//h2[contains(text(), 'II часть ЕКП')]"))
        } catch (e: Exception) {
            throw ParserException("Не удалось найти 'II часть ЕКП'")
        }
    }

    private fun getWrapper(element: WebElement): WebElement {
        return try {
            element.findElement(By.xpath("./ancestor::div[@class='wrapper']"))
        } catch (e: Exception) {
            throw ParserException("Не удалось найти wrapper")
        }
    }

    private fun getYearsContainer(element: WebElement): List<WebElement> {
        return try {
            element.findElements(By.className("cursor-pointer"))
        } catch (e: Exception) {
            throw ParserException("Не удалось найти папки")
        }
    }

    fun getLink(driver: WebDriver): MutableMap<String, String> {
        val url = "https://www.minsport.gov.ru/activity/government-regulation/edinyj-kalendarnyj-plan/"
        openPage(driver, url)

        val resultLinks: MutableMap<String, String> = mutableMapOf()
        val head = getHead(driver)
        val wrapper = getWrapper(head)
        val yearContainers = getYearsContainer(wrapper)


        yearContainers.forEach { it ->
            it.findElement(By.className("folder-image-conteiner")).click()
            val files = it.findElements(By.className("file-item")).forEach { file ->
                val title = file.findElement(By.tagName("p")).text
                if (title.contains("Единый календарный план")) {
                    val links = file.findElements(By.tagName("a")).forEach { link ->
                        val href = link.getAttribute("href")
                        if (href != null && href.endsWith(".pdf")) {
                            resultLinks.put(title, href)
                        }
                    }
                }

            }
        }

        return resultLinks
    }
}
