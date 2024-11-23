package com.govzcode.sportevents.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import com.govzcode.sportevents.exception.ParserException
import org.springframework.beans.factory.ObjectProvider


@Component
@Scope("prototype")
class ParseLinks(
    private val webDriverProvider: ObjectProvider<WebDriverComponent>,
    private val parseLinksService: ParseLinksService
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    fun parseLinks(): MutableMap<String, String> {
        try {
            var links = mutableMapOf<String, String>()
            val webDriverComponent = webDriverProvider.getObject()
            webDriverComponent.run { driver ->
                links = parseLinksService.getLink(driver)
            }
            return links
        } catch (e: Exception) {
            logger.error("Ошибка при парсинге ссылок: ${e.message}")
            throw ParserException("Error parse Links")
        }
    }
}