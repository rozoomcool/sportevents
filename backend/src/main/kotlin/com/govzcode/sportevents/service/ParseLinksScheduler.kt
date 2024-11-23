package com.govzcode.sportevents.service

import com.govzcode.sportevents.entity.EventLink
import com.govzcode.sportevents.util.ParseLinks
import com.govzcode.sportevents.util.ProcessPdfLink
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component


@Component
class ParseLinksScheduler(
    private val eventLinkService: EventLinkService,
    private val parseLinksProvider: ObjectProvider<ParseLinks>,
    private val processPdfLinkProvider: ObjectProvider<ProcessPdfLink>,
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Scheduled(fixedRate = 5000)
    fun getLinks() {
        try {
            logger.info("START PARSE LINKS")
            val parseLinksCmp = parseLinksProvider.getObject()
            val links = parseLinksCmp.parseLinks()
            links.forEach {
                eventLinkService.create(EventLink(title = it.key, link = it.value))
            }
        } catch (e: Exception) {
            logger.error(e.message)
        }
    }

    @Scheduled(fixedRate = 30000)
    fun getSportEvent() {
        try {
            logger.info("START PARSE LINKS")
            val processPdfLink = processPdfLinkProvider.getObject()
            val link = eventLinkService.findUnChecked() ?: return
            processPdfLink.getPdfData(link.link)

        } catch (e: Exception) {
            logger.error(e.message)
        }
    }


}