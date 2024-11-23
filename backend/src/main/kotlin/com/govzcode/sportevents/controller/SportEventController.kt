package com.govzcode.sportevents.controller

import com.govzcode.sportevents.dto.PageableDto
import com.govzcode.sportevents.dto.SportEventDto
import com.govzcode.sportevents.entity.SportEvent
import com.govzcode.sportevents.repository.SportEventRepository
import com.govzcode.sportevents.service.SportEventService
import com.turkraft.springfilter.boot.Filter
import jakarta.validation.Valid
import org.springframework.data.domain.Example
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(name = "api/v1/events")
class SportEventController(
    private val sportEventService: SportEventService
) {
    @GetMapping("/search")
    fun search(@Filter spec: Example<SportEvent>, pageable: Pageable): PageableDto<SportEvent> {
        return sportEventService.filter(spec, pageable)
    }

    @PostMapping
    fun create(@RequestBody @Valid sportEventDto: SportEventDto): SportEvent {
        return sportEventService.createSportEvent(sportEventDto)
    }

}