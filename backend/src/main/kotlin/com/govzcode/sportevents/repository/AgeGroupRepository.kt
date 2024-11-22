package com.govzcode.sportevents.repository

import com.govzcode.sportevents.entity.AgeGroup
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AgeGroupRepository: JpaRepository<AgeGroup, Long> {
    fun findByName(name: String): AgeGroup?
}