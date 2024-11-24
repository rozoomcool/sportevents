package com.govzcode.sportevents.controller

import com.govzcode.sportevents.dto.PageableDto
import com.govzcode.sportevents.dto.SportEventDto
import com.govzcode.sportevents.entity.Country
import com.govzcode.sportevents.entity.Region
import com.govzcode.sportevents.entity.SportEvent
import com.govzcode.sportevents.service.SportEventService
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import jakarta.validation.Valid
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.web.bind.annotation.*
import java.text.SimpleDateFormat
import java.util.*


@Slf4j
@RestController
@RequestMapping("api/v1/events")
class SportEventController(
    private val sportEventService: SportEventService,
    private val entityManager: EntityManager
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/search")
    fun search(
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "1") page: Int
    ): PageableDto<SportEvent> {
        return sportEventService.page(PageRequest.of(page, size))
    }

    @GetMapping("/filter")
    fun findSportEventsByCriteria(
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam title: String?,
        @RequestParam sportTitle: String?,
        @RequestParam startDate: Date?,
        @RequestParam endDate: Date?,
        @RequestParam country: String?,
        @RequestParam regions: Set<String>?,
    ): Iterable<SportEvent> {
        return sportEventService.findSportEventsByCriteria(
            title, sportTitle, startDate, endDate, country, regions,
            PageRequest.of(1, 10)
        )
    }


    @PostMapping
    fun create(@RequestBody @Valid sportEventDto: SportEventDto): SportEvent {
        return sportEventService.createSportEvent(sportEventDto)
    }

}

fun buildSportEventSpecification(params: Map<String, String>): Specification<SportEvent> {
    return Specification { root, query, criteriaBuilder ->
        var predicates: Predicate = criteriaBuilder.conjunction()

        params.forEach { (field, value) ->
            when (field) {
                "title" -> {
                    predicates = criteriaBuilder.and(
                        predicates,
                        criteriaBuilder.like(root.get<String>("title"), value)
                    )
                }

                "sportTitle" -> {
                    predicates = criteriaBuilder.and(
                        predicates,
                        criteriaBuilder.like(root.get<String>("sportTitle"), "%$value%")
                    )
                }

                "ekpId" -> {
                    predicates = criteriaBuilder.and(
                        predicates,
                        criteriaBuilder.equal(root.get<String>("ekpId"), value)
                    )
                }

                "startDate" -> {
                    val startDate = value.toDate()
                    predicates = criteriaBuilder.and(
                        predicates,
                        criteriaBuilder.greaterThanOrEqualTo(root.get<Date>("startsDate"), startDate)
                    )
                }

                "endDate" -> {
                    val endDate = value.toDate()
                    predicates = criteriaBuilder.and(
                        predicates,
                        criteriaBuilder.lessThanOrEqualTo(root.get<Date>("endsDate"), endDate)
                    )
                }

                "country" -> {
                    predicates = criteriaBuilder.and(
                        predicates,
                        criteriaBuilder.equal(root.get<Country>("country").get<String>("name"), "%$value%")
                    )
                }

                "regions" -> {
                    val regionNames = value.split(",")

                    val regionPredicate: Predicate = root.get<Set<Region>>("regions")
                        .get<String>("name")
                        .`in`(regionNames)

                    predicates = criteriaBuilder.and(predicates, regionPredicate)
                }
            }
        }

        return@Specification predicates
    }
}

fun String.toDate(): Date? {
    val format = SimpleDateFormat("yyyy-MM-dd") // Используем стандартный формат
    return try {
        format.parse(this)
    } catch (e: Exception) {
        null
    }
}