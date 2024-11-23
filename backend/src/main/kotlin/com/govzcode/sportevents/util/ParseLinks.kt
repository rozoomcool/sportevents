package com.govzcode.sportevents.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import com.govzcode.sportevents.exception.ParserException


@Component
@Scope("prototype")
class ParseLinks(
    private val webDriverComponent: WebDriverComponent,
    private val parseLinksService: ParseLinksService
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    fun parseLinks(): MutableMap<String, String> {
        try {
            var links = mutableMapOf<String, String>()
            webDriverComponent.run { driver ->
                links = parseLinksService.getLink(driver)
            }
            return links
        } catch (e: Exception) {
            logger.error("Ошибка при парсинге ссылок")
            throw ParserException("Error parse Links")
        }
    }
}