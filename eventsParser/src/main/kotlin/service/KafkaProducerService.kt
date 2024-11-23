package service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

//@Service
//class KafkaProducerService(
//    private val kafkaTemplate: KafkaTemplate<String, String>
//) {
//    private val logger = LoggerFactory.getLogger(KafkaProducerService::class.java)
//
//    fun sendMessage(topic: String, key: String, message: String) {
//        logger.info("Отправка сообщения в Kafka: topic=$topic, key=$key, message=$message")
//        kafkaTemplate.send(topic, key, message).addCallback(
//            { logger.info("Сообщение успешно отправлено: topic=$topic, key=$key") },
//            { ex -> logger.error("Ошибка при отправке сообщения: ${ex.message}") }
//        )
//    }
//}