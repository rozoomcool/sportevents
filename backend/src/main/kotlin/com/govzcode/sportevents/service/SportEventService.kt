package com.govzcode.sportevents.service

import com.govzcode.sportevents.dto.PageableDto
import com.govzcode.sportevents.dto.SportEventDto
import com.govzcode.sportevents.entity.*
import com.govzcode.sportevents.repository.*
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.*
import jakarta.transaction.Transactional
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class SportEventService(
    private val sportEventRepository: SportEventRepository,
    private val countryRepository: CountryRepository,
    private val regionRepository: RegionRepository,
    private val targetAuditoryRepository: TargetAuditoryRepository,
    private val entityManager: EntityManager
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

    fun getRandom(): SportEvent? {
        val max = sportEventRepository.count()
        return sportEventRepository.findById(Random().nextLong(max)).getOrNull()
    }

    fun findSportEventsByCriteria(
        title: String?,
        sportTitle: String?,
        startDate: Date?,
        endDate: Date?,
        country: String?,
        regions: Set<String>?,
        pageable: Pageable
    ): List<SportEvent> {

        // Получаем CriteriaBuilder
        val cb: CriteriaBuilder = entityManager.criteriaBuilder

        // Создаем запрос
        val query: CriteriaQuery<SportEvent> = cb.createQuery(SportEvent::class.java)

        // Корневой объект (SportEvent)
        val sportEvent: Root<SportEvent> = query.from(SportEvent::class.java)

        // Список условий для фильтрации
        val predicates: MutableList<Predicate> = mutableListOf()

        // Условие для title
        title?.let { predicates.add(cb.like(sportEvent.get<String>("title"), "%$it%")) }

        // Условие для sportTitle
        sportTitle?.let { predicates.add(cb.like(sportEvent.get<String>("sportTitle"), "%$it%")) }

        // Условие для startDate
        startDate?.let { predicates.add(cb.greaterThanOrEqualTo(sportEvent.get("startsDate"), startDate)) }

        // Условие для endDate
        endDate?.let { predicates.add(cb.lessThanOrEqualTo(sportEvent.get("endsDate"), endDate)) }

        // Условие для countryId
        country?.let {
            val countryJoin: Join<SportEvent, Country> = sportEvent.join<SportEvent, Country>("country")
            predicates.add(cb.equal(countryJoin.get<String>("name"), it))
        }

        // Условие для regionIds
        regions?.let {
            val regionJoin: Join<SportEvent, Region> = sportEvent.join<SportEvent, Region>("regions")
            predicates.add(regionJoin.get<String>("name").`in`(it))
        }

        // Применяем все условия
        query.select(sportEvent).where(cb.and(*predicates.toTypedArray()))

        // Создаем запрос с пагинацией
        val typedQuery = entityManager.createQuery(query)
        typedQuery.firstResult = pageable.offset.toInt()
        typedQuery.maxResults = pageable.pageSize

        // Выполняем запрос и возвращаем результат
        return typedQuery.resultList
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

        val disciplines = sportEventDto.targetAudience.map {
            targetAuditoryRepository.findByName(it) ?: targetAuditoryRepository.save(TargetAuditory(it))
        }

        // Создаем SportEvent и сохраняем
        val sportEvent = SportEvent(
            ekpId = sportEventDto.ekpId,
            startsDate = sportEventDto.startsDate,
            endsDate = sportEventDto.endsDate,
            numberOfParticipant = sportEventDto.numberOfParticipants,
            targetAuditory = disciplines.toMutableList(),
            country = country,
            regions = regions.toMutableSet(),
            title = sportEventDto.title,
            sportTitle = sportEventDto.sportTitle
        )

        return sportEventRepository.save(sportEvent)
    }
}