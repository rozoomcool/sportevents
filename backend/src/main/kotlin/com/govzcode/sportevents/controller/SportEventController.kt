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
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1/events")
class SportEventController(
    private val sportEventService: SportEventService
) {
    @GetMapping("/search")
    fun search(
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "1") page: Int,
    ): PageableDto<SportEvent> {
        return sportEventService.page(PageRequest.of(page, size))
    }


    @PostMapping
    fun create(@RequestBody @Valid sportEventDto: SportEventDto): SportEvent {
        return sportEventService.createSportEvent(sportEventDto)
    }

}