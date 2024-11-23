package com.govzcode.sportevents.service

import com.govzcode.sportevents.entity.EventLink
import com.govzcode.sportevents.exception.EntityNotFoundException
import com.govzcode.sportevents.repository.EventLinkRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class EventLinkService(
    private val eventLinkRepository: EventLinkRepository
) {
    @Transactional
    fun create(event: EventLink) {
        if(eventLinkRepository.findByTitle(event.title) != null) {
            return
        }
        eventLinkRepository.save(event)
    }

    @Transactional
    fun updateToChecked(eventId: Long) {
        val entity = eventLinkRepository.findById(eventId).orElseThrow{ throw EntityNotFoundException("Entity not found") }
        entity.apply { checked = true }
        eventLinkRepository.save(entity)
    }

    fun findUnChecked(): EventLink? {
        val data = eventLinkRepository.findAll().filter { !it.checked }
        if (data.isNotEmpty()) {
            return data[0]
        } else {
            return null
        }
    }
}