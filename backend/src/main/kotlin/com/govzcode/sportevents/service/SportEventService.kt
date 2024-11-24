package com.govzcode.sportevents.service

import com.govzcode.sportevents.dto.PageableDto
import com.govzcode.sportevents.dto.SportEventDto
import com.govzcode.sportevents.entity.*
import com.govzcode.sportevents.repository.*
import jakarta.transaction.Transactional
import org.springframework.data.domain.Example
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

@Service
class SportEventService(
    private val sportEventRepository: SportEventRepository,
    private val countryRepository: CountryRepository,
    private val regionRepository: RegionRepository,
    private val disciplineRepository: DisciplineRepository,
    private val targetAuditoryRepository: TargetAuditoryRepository,
) {

    fun filter(spec: Specification<SportEvent>, page: Pageable): PageableDto<SportEvent> {
        val entity = sportEventRepository.findAll(spec, page)
        return PageableDto(
            content = entity.content,
            totalElements = entity.totalElements,
            page = entity.number,
            size = entity.size,
            totalPages = entity.count()
        )
    }

    fun page(page: Pageable): PageableDto<SportEvent> {
        val entity = sportEventRepository.findAll(null, page)
        return PageableDto(
            content = entity.content,
            totalElements = entity.totalElements,
            page = entity.number,
            size = entity.size,
            totalPages = entity.totalPages
        )
    }

    fun all(): Iterable<SportEvent> = sportEventRepository.findAll()

    @Transactional
    fun createSportEvent(
        sportEventDto: SportEventDto
    ): SportEvent {
        val country = countryRepository.findByName(sportEventDto.country)
            ?: countryRepository.save(Country(name = sportEventDto.country))

        val regions = sportEventDto.regions.map {
            regionRepository.findByName(it) ?: regionRepository.save(Region(it))
        }

        val disciplines = sportEventDto.disciplines.map {
            disciplineRepository.findByName(it) ?: disciplineRepository.save(Discipline(it))
        }

        val targetAuditory = targetAuditoryRepository.findByName(sportEventDto.targetAuditory)
            ?: targetAuditoryRepository.save(TargetAuditory(sportEventDto.targetAuditory))

        // Создаем SportEvent и сохраняем
        val sportEvent = SportEvent(
            ekpId = sportEventDto.ekpId,
            startsDate = sportEventDto.startsDate,
            endsDate = sportEventDto.endsDate,
            numberOfParticipant = sportEventDto.numberOfParticipants,
            disciplines = disciplines.toMutableList(),
            targetAuditory = targetAuditory,
            country = country,
            regions = regions.toMutableSet(),
            title = sportEventDto.title,
            sportTitle = sportEventDto.sportTitle
        )

        return sportEventRepository.save(sportEvent)
    }
}