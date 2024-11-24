package com.govzcode.sportevents.service

import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class NotificationService(private val messagingTemplate: SimpMessagingTemplate) {

    fun sendNotificationToAllUsers(notification: String) {
        messagingTemplate.convertAndSend("/topic/notifications", notification)
    }
}