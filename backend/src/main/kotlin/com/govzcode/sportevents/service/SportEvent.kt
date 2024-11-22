package com.govzcode.sportevents.service

import com.govzcode.sportevents.repository.SportEventRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class SportEvent(
        private val sportEventRepository: SportEventRepository
) {
    @Transactional
    fun create() {

    }
}