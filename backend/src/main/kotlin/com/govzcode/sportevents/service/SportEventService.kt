package com.govzcode.sportevents.service

import com.govzcode.sportevents.dto.PageableDto
import com.govzcode.sportevents.dto.SportEventDto
import com.govzcode.sportevents.entity.*
import com.govzcode.sportevents.repository.*
import jakarta.transaction.Transactional
import org.springframework.data.domain.Example
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class SportEventService(
    private val sportEventRepository: SportEventRepository,
    private val countryRepository: CountryRepository,
    private val regionRepository: RegionRepository,
    private val cityRepository: CityRepository,
    private val disciplineRepository: DisciplineRepository,
    private val programRepository: ProgramRepository,
    private val ageGroupRepository: AgeGroupRepository
) {

    fun filter(spec: Example<SportEvent>, page: Pageable): PageableDto<SportEvent> {
        val entity = sportEventRepository.findAll(spec, page)
        return PageableDto(
            content = entity.content,
            totalElements = entity.totalElements,
            page = entity.number,
            size = entity.size,
            totalPages = entity.count()
        )
    }

    @Transactional
    fun createSportEvent(
        sportEventDto: SportEventDto
    ): SportEvent {
        val country = countryRepository.findByName(sportEventDto.country)
            ?: countryRepository.save(Country(name = sportEventDto.country))

        val region = regionRepository.findByNameAndCountry(sportEventDto.region, country)
            ?: regionRepository.save(Region(name = sportEventDto.region, country = country))

        val city = cityRepository.findByNameAndRegion(sportEventDto.city, region)
            ?: cityRepository.save(City(name = sportEventDto.city, region = region))

        val discipline = disciplineRepository.findByName(sportEventDto.discipline)
            ?: disciplineRepository.save(Discipline(name = sportEventDto.discipline))

        val ageGroup = ageGroupRepository.findByName(sportEventDto.ageGroup)
            ?: ageGroupRepository.save(AgeGroup(sportEventDto.ageGroup))

        val program = programRepository.findByName(sportEventDto.program)
            ?: programRepository.save(Program(sportEventDto.program))

        // Создаем SportEvent и сохраняем
        val sportEvent = SportEvent(
            ekpId = sportEventDto.ekpId,
            country = country,
            region = region,
            city = city,
            startsDate = sportEventDto.startsDate,
            endsDate = sportEventDto.endsDate,
            numberOfParticipant = sportEventDto.numberOfParticipants,
            discipline = discipline,
            ageGroup = ageGroup,
            program = program
        )

        return sportEventRepository.save(sportEvent)
    }
}