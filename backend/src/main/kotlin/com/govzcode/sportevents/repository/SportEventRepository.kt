package com.govzcode.sportevents.repository

import com.govzcode.sportevents.entity.SportEvent
import com.turkraft.springfilter.boot.Filter
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.CrudRepository
import org.springframework.data.web.PagedModel
import org.springframework.stereotype.Repository

@Repository
interface SportEventRepository : CrudRepository<SportEvent, Long>, JpaSpecificationExecutor<SportEvent> {
    fun findAll(pageable: Pageable): Page<SportEvent>
}