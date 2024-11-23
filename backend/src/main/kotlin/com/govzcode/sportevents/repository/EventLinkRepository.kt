package com.govzcode.sportevents.repository

import com.govzcode.sportevents.entity.EventLink
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface EventLinkRepository: CrudRepository<EventLink, Long> {
}