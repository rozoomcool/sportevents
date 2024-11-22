package com.govzcode.sportevents.controller

import com.govzcode.sportevents.entity.SportEvent
import com.govzcode.sportevents.repository.SportEventRepository
import com.turkraft.springfilter.boot.Filter
import org.springframework.data.domain.Example
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(name = "events")
class SportEventController(
        private val sportEventRepository: SportEventRepository
) {
    @GetMapping("/search")
    fun search(@Filter spec: Example<SportEvent>, pageable: Pageable): Page<SportEvent> {
        return sportEventRepository.findAll(spec, pageable)
    }

}