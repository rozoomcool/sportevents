package com.govzcode.sportevents.service

import com.govzcode.sportevents.entity.EventLink
import com.govzcode.sportevents.util.ParseLinks
import com.govzcode.sportevents.util.ProcessPdfLink
//import com.govzcode.sportevents.util.ProcessPdfLinkDUPL
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component


@Component
class ParseLinksScheduler(
    private val eventLinkService: EventLinkService,
    private val parseLinksProvider: ObjectProvider<ParseLinks>,
    private val processPdfLinkProvider: ObjectProvider<ProcessPdfLink>,
    private val sportEventService: SportEventService,
    private val template: SimpMessagingTemplate,
    private val userLocalRepService: UserLocalRepService
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Scheduled(fixedRate = 10000)
    fun some() {
        userLocalRepService.getAll().forEach{
            template.convertAndSendToUser(it, "/queue/notification", sportEventService.getRandom() ?: "Hello")
        }
    }

//    @Scheduled(fixedRate = 5000)
//    fun getLinks() {
//        try {
//            logger.info("START PARSE LINKS")
//            val parseLinksCmp = parseLinksProvider.getObject()
//            val links = parseLinksCmp.parseLinks()
//            links.forEach {
//                eventLinkService.create(EventLink(title = it.key, link = it.value))
//            }
//        } catch (e: Exception) {
//            logger.error(e.message)
//        }
//    }

    @Scheduled(fixedRate = 30000)
    fun getSportEvent() {
        try {
            logger.info("START PROCESS PDF LINKS")
            val processPdfLink = processPdfLinkProvider.getObject()
//            val link = eventLinkService.findUnChecked() ?: return
            val data = processPdfLink.getPdfData("https://storage.minsport.gov.ru/cms-uploads/cms/II_chast_EKP_2024_14_11_24_65c6deea36.pdf"){
                try {
                    sportEventService.createSportEvent(it)
                } catch (e: Exception) {
                    logger.error("${e.message}")
                }
            }
            logger.info("PARSE ${data.size} data")
            data.forEach {
                try {
                    sportEventService.createSportEvent(it)
                } catch (e: Exception) {
                    logger.error("${e.message}")
                }
            }
        } catch (e: Exception) {
            logger.error("ERROR IN PROCESS PDF ${e.message}")
        }
    }


}